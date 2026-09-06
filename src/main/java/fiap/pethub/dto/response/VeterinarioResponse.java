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
public class VeterinarioResponse {

    @Schema(description = "ID do veterinário", example = "1")
    private Long id;

    @Schema(description = "Nome completo", example = "Dr. Carlos Souza")
    private String nome;

    @Schema(description = "Número do CRMV", example = "CRMV-SP 12345")
    private String crmv;

    @Schema(description = "Especialidade", example = "Clínica Geral")
    private String especialidade;

    @Schema(description = "Email", example = "carlos@vetclinica.com")
    private String email;

    @Schema(description = "Telefone", example = "(11) 99999-0000")
    private String telefone;

    @Schema(description = "Indica se o veterinário está ativo", example = "true")
    private Boolean ativo;

    @Schema(description = "Nome da unidade veterinária vinculada", example = "Clínica PetVida")
    private String nomeUnidade;
}

