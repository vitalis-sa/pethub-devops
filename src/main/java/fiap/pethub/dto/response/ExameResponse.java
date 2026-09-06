package fiap.pethub.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExameResponse {

    @Schema(description = "Identificador do exame", example = "1")
    private Long id;

    @Schema(description = "Tipo do exame", example = "Hemograma")
    private String tipo;

    @Schema(description = "Data do exame", example = "2026-06-01")
    private LocalDate data;

    @Schema(description = "Resultado do exame")
    private String resultado;

    @Schema(description = "Caminho ou URL do arquivo de resultado")
    private String arquivoResultado;

    @Schema(description = "Nome do pet", example = "Rex")
    private String nomePet;

    @Schema(description = "Data e hora da consulta vinculada", example = "2026-06-01T10:00:00")
    private LocalDateTime dataConsulta;
}


