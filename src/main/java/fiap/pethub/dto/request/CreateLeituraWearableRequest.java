package fiap.pethub.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLeituraWearableRequest {

    @NotNull(message = "petId é obrigatório")
    private Long petId;

    @NotNull(message = "Timestamp é obrigatório")
    private LocalDateTime timestamp;

    @NotNull(message = "Temperatura corporal é obrigatória")
    private Double temperaturaCorporal;

    @NotNull(message = "Frequência cardíaca é obrigatória")
    private Integer frequenciaCardiaca;

    private Boolean anomaliaDetectada;
    private String tipoAnomalia;
}

