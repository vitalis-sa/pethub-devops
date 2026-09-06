package fiap.pethub.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDiagnosticoRequest {

    @NotNull(message = "petId é obrigatório")
    private Long petId;

    @NotNull(message = "consultaId é obrigatório")
    private Long consultaId;

    @NotNull(message = "Data é obrigatória")
    private LocalDateTime data;

    private String sintoma1;
    private String sintoma2;
    private String sintoma3;
    private String sintoma4;
    private String duracaoSintomas;

    private Boolean perdaApetite;
    private Boolean vomito;
    private Boolean diarreia;
    private Boolean tosse;
    private Boolean dificuldadeRespiratoria;
    private Boolean claudicacao;
    private Boolean lesoesPele;
    private Boolean secrecaoNasal;
    private Boolean secrecaoOcular;

    @DecimalMin(value = "35.0", message = "Temperatura deve ser >= 35.0°C")
    @DecimalMax(value = "43.0", message = "Temperatura deve ser <= 43.0°C")
    private Double temperaturaCorporal;

    @Min(value = 20, message = "Frequência cardíaca deve ser >= 20 bpm")
    @Max(value = 300, message = "Frequência cardíaca deve ser <= 300 bpm")
    private Integer frequenciaCardiaca;

    private String doencaPredita;

    @DecimalMin(value = "0.0", message = "Confiança deve ser >= 0.0")
    @DecimalMax(value = "1.0", message = "Confiança deve ser <= 1.0")
    private Double confiancaPredicao;

    private String analiseGenAI;
}

