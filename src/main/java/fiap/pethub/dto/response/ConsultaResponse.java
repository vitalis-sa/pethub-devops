package fiap.pethub.dto.response;

import fiap.pethub.enums.StatusConsulta;
import fiap.pethub.enums.TipoConsulta;
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
public class ConsultaResponse {

    @Schema(description = "ID da consulta", example = "1")
    private Long id;

    @Schema(description = "Data e hora", example = "2026-06-01T10:00:00")
    private LocalDateTime dataHora;

    @Schema(description = "Tipo da consulta", example = "PRESENCIAL")
    private TipoConsulta tipo;

    @Schema(description = "Observações")
    private String observacoes;

    @Schema(description = "Status", example = "AGENDADA")
    private StatusConsulta status;

    @Schema(description = "Nome do pet", example = "Rex")
    private String nomePet;

    @Schema(description = "Nome do veterinário", example = "Dr. Carlos Souza")
    private String nomeVeterinario;

    @Schema(description = "Nome da unidade veterinária", example = "Clínica PetVida")
    private String nomeUnidade;
}

