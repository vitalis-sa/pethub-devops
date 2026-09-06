package fiap.pethub.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    @Schema(description = "Código HTTP", example = "404")
    private int status;
    @Schema(description = "Mensagem de erro")
    private String message;
}

