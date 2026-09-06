package fiap.pethub.dto.response;

import fiap.pethub.enums.StatusLembrete;
import fiap.pethub.enums.TipoLembrete;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LembreteResponse {

    @Schema(description = "ID do lembrete")
    private Long id;

    @Schema(description = "ID do responsável")
    private Long responsavelId;

    @Schema(description = "Nome do responsável")
    private String nomeResponsavel;

    @Schema(description = "ID do pet")
    private Long petId;

    @Schema(description = "Nome do pet")
    private String nomePet;

    @Schema(description = "Tipo do lembrete", example = "VACINA")
    private TipoLembrete tipo;

    @Schema(description = "Data agendada", example = "2026-06-01")
    private LocalDate dataAgendada;

    @Schema(description = "Mensagem")
    private String mensagem;

    @Schema(description = "Status do lembrete", example = "PENDENTE")
    private StatusLembrete status;

    @Schema(description = "ID da entidade referenciada")
    private Long referenciaId;

    @Schema(description = "Tipo da entidade referenciada")
    private String referenciaTipo;

    @Schema(description = "Data de criação")
    private LocalDateTime createdAt;
}

