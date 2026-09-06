package fiap.pethub.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LeituraWearableRequest {

    @NotNull(message = "petId é obrigatório")
    @Schema(description = "ID do pet (felino)", example = "1")
    private Long petId;

    @NotNull(message = "Timestamp é obrigatório")
    @Schema(description = "Data e hora da leitura", example = "2026-06-01T10:00:00")
    private LocalDateTime timestamp;

    @NotNull(message = "Consumo registrado é obrigatório")
    @Positive(message = "Consumo deve ser positivo")
    @Schema(description = "Quantidade de água consumida nessa leitura (ml)", example = "35.0")
    private Double consumoMlRegistrado;

    @NotNull(message = "Meta diária é obrigatória")
    @Positive(message = "Meta diária deve ser positiva")
    @Schema(description = "Meta diária de consumo de água (ml) — recomendado ~60ml/kg para felinos", example = "240.0")
    private Double metaDiariaML;
}
