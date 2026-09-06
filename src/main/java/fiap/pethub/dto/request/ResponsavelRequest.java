package fiap.pethub.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsavelRequest {

    @NotBlank(message = "nome é obrigatório")
    @Size(max = 150)
    @Schema(description = "Nome completo do responsável", example = "João Silva")
    private String nome;

    @NotBlank(message = "cpf é obrigatório")
    @Size(min = 11, max = 11, message = "CPF deve ter 11 dígitos")
    @Schema(description = "CPF do responsável (apenas dígitos)", example = "12345678901")
    private String cpf;

    @NotBlank(message = "email é obrigatório")
    @Email(message = "email inválido")
    @Schema(description = "E-mail do responsável", example = "joao@email.com")
    private String email;

    @NotBlank(message = "senha é obrigatória")
    @Schema(description = "Senha do responsável")
    private String senha;

    @Schema(description = "Status ativo do responsável", example = "true")
    private Boolean ativo;
}

