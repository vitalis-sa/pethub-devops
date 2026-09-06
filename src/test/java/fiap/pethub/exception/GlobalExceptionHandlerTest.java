package fiap.pethub.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * O handler declara um @ExceptionHandler(Exception.class) que captura tudo,
 * inclusive as exceções que o próprio Spring lança para sinalizar 404. Com isso
 * toda rota inexistente chega ao cliente como 500, o que engana quem consome a
 * API e mascara erro de digitação de rota durante o desenvolvimento.
 *
 * O teste usa o ExceptionHandlerMethodResolver — a mesma classe que o Spring MVC
 * usa em produção para escolher qual método trata cada exceção — em vez de subir
 * a camada web, que exigiria dependências de teste ausentes neste projeto.
 */
class GlobalExceptionHandlerTest {

    private final ExceptionHandlerMethodResolver resolver =
            new ExceptionHandlerMethodResolver(GlobalExceptionHandler.class);

    private HttpStatus statusPara(Exception excecao) throws Exception {
        Method metodo = resolver.resolveMethod(excecao);
        assertThat(metodo).as("nenhum handler resolvido para %s", excecao.getClass()).isNotNull();
        Object resposta = metodo.invoke(new GlobalExceptionHandler(), excecao);
        return HttpStatus.valueOf(((ResponseEntity<?>) resposta).getStatusCode().value());
    }

    @Test
    void rotaInexistenteDeveVirar404() throws Exception {
        NoResourceFoundException rotaInexistente =
                new NoResourceFoundException(HttpMethod.GET, "/api/rota-que-nao-existe", "api");

        assertThat(statusPara(rotaInexistente)).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void metodoHttpNaoSuportadoDeveVirar405() throws Exception {
        var metodoErrado = new HttpRequestMethodNotSupportedException("PATCH");

        assertThat(statusPara(metodoErrado)).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Recurso alheio responde 404, nao 403: 403 confirmaria que aquele id
     * existe e permitiria enumerar o banco tutor por tutor.
     */
    @Test
    void recursoDeOutroUsuarioVira404ENao403() throws Exception {
        var alheio = new AcessoNegadoException("Recurso nao encontrado");

        assertThat(statusPara(alheio)).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void falhaInesperadaContinuaVirando500() throws Exception {
        assertThat(statusPara(new IllegalStateException("boom")))
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
