package fiap.pethub.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetResponse {

    @Schema(description = "ID do pet", example = "1")
    private Long id;

    @Schema(description = "Nome do pet", example = "Rex")
    private String nome;

    @Schema(description = "Espécie", example = "Cão")
    private String especie;

    @Schema(description = "Raça", example = "Labrador")
    private String raca;

    @Schema(description = "Idade em anos", example = "3")
    private Integer idade;

    @Schema(description = "Peso em kg", example = "25.5")
    private Double peso;

    @Schema(description = "Gênero", example = "Macho")
    private String genero;

    @Schema(description = "Nome do responsável", example = "João Silva")
    private String nomeResponsavel;

    @Schema(description = "Nome do veterinário responsável", example = "Dr. Carlos Souza")
    private String nomeVeterinarioResponsavel;
}

