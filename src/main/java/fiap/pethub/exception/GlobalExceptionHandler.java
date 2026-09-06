package fiap.pethub.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldErrorDetail> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new FieldErrorDetail(e.getField(), e.getDefaultMessage()))
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrorResponse(HttpStatus.BAD_REQUEST.value(), "Erro de validação", errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Corpo da requisição inválido ou mal formado"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String msg = String.format("Parâmetro '%s' com valor '%s' é inválido", ex.getName(), ex.getValue());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), msg));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<FieldErrorDetail> errors = ex.getConstraintViolations().stream()
                .map(v -> new FieldErrorDetail(v.getPropertyPath().toString(), v.getMessage()))
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrorResponse(HttpStatus.BAD_REQUEST.value(), "Parâmetro inválido", errors));
    }

    /**
     * Recurso que existe mas pertence a outro usuário responde 404, e não 403,
     * porque 403 confirmaria a existência do id e permitiria enumerar o banco.
     * A mensagem é deliberadamente idêntica à de recurso inexistente.
     */
    @ExceptionHandler(AcessoNegadoException.class)
    public ResponseEntity<ErrorResponse> handleAcessoNegado(AcessoNegadoException ex) {
        log.warn("Acesso negado a recurso de outro usuário: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Recurso não encontrado"));
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ErrorResponse> handleEmailDuplicado(EmailJaCadastradoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    /**
     * Não distinguimos "senha errada" de "email inexistente" na resposta: dizer
     * qual dos dois falhou permitiria descobrir quais emails estão cadastrados.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleFalhaDeAutenticacao(AuthenticationException ex) {
        log.warn("Autenticação recusada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Credenciais inválidas"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(HttpStatus.CONFLICT.value(), "Violação de integridade de dados"));
    }

    /**
     * Exceções do próprio Spring que já carregam o status HTTP correto — rota
     * inexistente (404), método não suportado (405), mídia não aceita (415).
     * Sem este tratamento elas cairiam no bloco genérico abaixo e chegariam ao
     * cliente como 500, mascarando o erro real.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        if (ex instanceof org.springframework.web.ErrorResponse erro) {
            HttpStatusCode status = erro.getStatusCode();
            log.warn("Requisição rejeitada com {}: {}", status.value(), ex.getMessage());
            return ResponseEntity.status(status)
                    .body(new ErrorResponse(status.value(), descrever(status)));
        }
        log.error("Erro não tratado ao processar a requisição", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro interno do servidor"));
    }

    private String descrever(HttpStatusCode status) {
        return switch (status.value()) {
            case 404 -> "Recurso não encontrado";
            case 405 -> "Método HTTP não suportado para esta rota";
            case 415 -> "Formato de mídia não suportado";
            default -> "Requisição inválida";
        };
    }
}

