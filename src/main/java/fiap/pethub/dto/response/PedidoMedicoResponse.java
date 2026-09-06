package fiap.pethub.dto.response;

import fiap.pethub.enums.StatusPedidoMedico;
import fiap.pethub.enums.TipoPedidoMedico;
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
public class PedidoMedicoResponse {

    @Schema(description = "Identificador do pedido médico", example = "1")
    private Long id;

    @Schema(description = "Tipo do pedido", example = "EXAME")
    private TipoPedidoMedico tipo;

    @Schema(description = "Descrição", example = "Hemograma completo")
    private String descricao;

    @Schema(description = "Instruções", example = "Em jejum de 8h")
    private String instrucoes;

    @Schema(description = "Data limite", example = "2026-06-30")
    private LocalDate dataLimite;

    @Schema(description = "Status", example = "PENDENTE")
    private StatusPedidoMedico status;

    @Schema(description = "Nome do pet", example = "Rex")
    private String nomePet;

    @Schema(description = "Data de criação")
    private LocalDateTime createdAt;
}

