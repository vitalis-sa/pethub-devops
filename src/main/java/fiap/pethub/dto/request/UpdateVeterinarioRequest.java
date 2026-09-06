package fiap.pethub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVeterinarioRequest {

    private String nome;
    private String especialidade;

    @Email(message = "Email inválido")
    private String email;

    private String telefone;

    @Size(min = 6, message = "Senha deve ter ao menos 6 caracteres")
    private String senha;

    private Boolean ativo;
}

