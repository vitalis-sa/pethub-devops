package fiap.pethub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UnidadeVeterinarioRequest {

    private Long veterinarioId;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String logradouro;
    private String numero;
    private String bairro;
    private String cidade;

    @Size(max = 2, message = "Estado deve ter no máximo 2 caracteres (UF)")
    private String estado;

    @Size(max = 8, message = "CEP deve ter no máximo 8 dígitos")
    private String cep;
}

