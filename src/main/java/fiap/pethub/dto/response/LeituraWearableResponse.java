package fiap.pethub.dto.response;

import fiap.pethub.enums.TipoAlertaHidratacao;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeituraWearableResponse {

    @Schema(description = "Identificador da leitura", example = "1")
    private Long id;

    @Schema(description = "Nome do pet", example = "Mel")
    private String nomePet;

    @Schema(description = "Data e hora da leitura", example = "2026-06-01T10:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "Consumo registrado nessa leitura (ml)", example = "35.0")
    private Double consumoMlRegistrado;

    @Schema(description = "Consumo diário acumulado (ml)", example = "120.0")
    private Double consumoDiarioAcumulado;

    @Schema(description = "Meta diária de consumo (ml)", example = "240.0")
    private Double metaDiariaML;

    @Schema(description = "Percentual da meta diária atingido (%)", example = "50.0")
    private Double percentualMeta;

    @Schema(description = "Indica se um alerta foi gerado", example = "false")
    private Boolean alertaGerado;

    @Schema(description = "Tipo do alerta de hidratação", example = "BAIXO_CONSUMO")
    private TipoAlertaHidratacao tipoAlerta;

    @Schema(description = "Descrição do alerta", example = "Felino consumiu menos de 25% da meta diária.")
    private String descricaoAlerta;
}
