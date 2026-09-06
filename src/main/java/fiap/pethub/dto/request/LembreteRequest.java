package fiap.pethub.dto.request;

import fiap.pethub.enums.StatusLembrete;
import fiap.pethub.enums.TipoLembrete;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LembreteRequest {

    @NotNull(message = "responsavelId é obrigatório")
    @Schema(description = "ID do responsável", example = "1")
    private Long responsavelId;

    @NotNull(message = "petId é obrigatório")
    @Schema(description = "ID do pet", example = "1")
    private Long petId;

    @NotNull(message = "tipo é obrigatório")
    @Schema(description = "Tipo do lembrete", example = "VACINA")
    private TipoLembrete tipo;

    @Schema(description = "Data agendada para o evento", example = "2026-06-01")
    private LocalDate dataAgendada;

    @NotBlank(message = "mensagem é obrigatória")
    @Schema(description = "Mensagem descritiva do lembrete")
    private String mensagem;

    @Schema(description = "Status do lembrete", example = "PENDENTE")
    private StatusLembrete status;

    @Schema(description = "ID da entidade referenciada (vacina, consulta, pedido, etc.)")
    private Long referenciaId;

    @Schema(description = "Tipo da entidade referenciada", example = "VacinaTratamento")
    private String referenciaTipo;
}
