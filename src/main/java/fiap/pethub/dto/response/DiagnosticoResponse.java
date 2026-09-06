package fiap.pethub.dto.response;

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
public class DiagnosticoResponse {

    @Schema(description = "Identificador do diagnóstico", example = "1")
    private Long id;

    @Schema(description = "Nome do pet", example = "Rex")
    private String nomePet;

    @Schema(description = "Data do diagnóstico", example = "2026-06-01T10:00:00")
    private LocalDateTime data;

    @Schema(description = "Sintoma 1", example = "Tosse")
    private String sintoma1;

    @Schema(description = "Sintoma 2")
    private String sintoma2;

    @Schema(description = "Sintoma 3")
    private String sintoma3;

    @Schema(description = "Sintoma 4")
    private String sintoma4;

    @Schema(description = "Duração dos sintomas", example = "3 dias")
    private String duracaoSintomas;

    @Schema(description = "Perda de apetite", example = "false")
    private Boolean perdaApetite;

    @Schema(description = "Vômito", example = "false")
    private Boolean vomito;

    @Schema(description = "Diarreia", example = "false")
    private Boolean diarreia;

    @Schema(description = "Tosse", example = "true")
    private Boolean tosse;

    @Schema(description = "Dificuldade respiratória", example = "false")
    private Boolean dificuldadeRespiratoria;

    @Schema(description = "Claudicação", example = "false")
    private Boolean claudicacao;

    @Schema(description = "Lesões na pele", example = "false")
    private Boolean lesoesPele;

    @Schema(description = "Secreção nasal", example = "false")
    private Boolean secrecaoNasal;

    @Schema(description = "Secreção ocular", example = "false")
    private Boolean secrecaoOcular;

    @Schema(description = "Temperatura corporal em °C", example = "38.5")
    private Double temperaturaCorporal;

    @Schema(description = "Frequência cardíaca em bpm", example = "90")
    private Integer frequenciaCardiaca;

    @Schema(description = "Doença predita pelo modelo ML", example = "Traqueobronquite")
    private String doencaPredita;

    @Schema(description = "Confiança da predição (0.0 a 1.0)", example = "0.87")
    private Double confiancaPredicao;

    @Schema(description = "Análise gerada pela GenAI")
    private String analiseGenAI;
}

