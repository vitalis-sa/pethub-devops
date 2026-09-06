package fiap.pethub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarioRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "CRMV é obrigatório")
    private String crmv;

    private String especialidade;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    private String telefone;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter ao menos 6 caracteres")
    private String senha;

    private Boolean ativo;

    private Long unidadeId;
}

