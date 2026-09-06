package fiap.pethub.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "email é obrigatório")
    @Email(message = "email inválido")
    @Schema(description = "E-mail cadastrado", example = "ana@pethub.com")
    private String email;

    @NotBlank(message = "senha é obrigatória")
    @Schema(description = "Senha do usuário")
    private String senha;
}
