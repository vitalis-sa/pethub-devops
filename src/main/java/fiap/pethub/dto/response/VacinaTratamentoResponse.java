package fiap.pethub.dto.response;

import fiap.pethub.enums.TipoVacinaTratamento;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacinaTratamentoResponse {

    @Schema(description = "Identificador da vacina ou tratamento", example = "1")
    private Long id;

    @Schema(description = "Tipo", example = "VACINA")
    private TipoVacinaTratamento tipo;

    @Schema(description = "Nome do produto/vacina", example = "V10")
    private String nome;

    @Schema(description = "Data de aplicação", example = "2026-05-01")
    private LocalDate dataAplicacao;

    @Schema(description = "Data da próxima dose", example = "2027-05-01")
    private LocalDate proximaDose;

    @Schema(description = "Dose", example = "1ª dose")
    private String dose;

    @Schema(description = "Observações")
    private String observacoes;

    @Schema(description = "Nome do pet", example = "Rex")
    private String nomePet;

    @Schema(description = "Nome do veterinário", example = "Dr. Carlos Souza")
    private String nomeVeterinario;
}

