package fiap.pethub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePetRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Espécie é obrigatória")
    private String especie;

    private String raca;
    private Integer idade;
    private Double peso;
    private String genero;

    @NotBlank(message = "CPF do responsável é obrigatório")
    private String responsavelCpf;

    private Long veterinarioResponsavelId;
}


