package fiap.pethub.dto.request;

import fiap.pethub.enums.StatusPedidoMedico;
import fiap.pethub.enums.TipoPedidoMedico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePedidoMedicoRequest {

    @NotNull(message = "consultaId é obrigatório")
    private Long consultaId;

    @NotNull(message = "petId é obrigatório")
    private Long petId;

    @NotNull(message = "Tipo é obrigatório")
    private TipoPedidoMedico tipo;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    private String instrucoes;
    private LocalDate dataLimite;
    private StatusPedidoMedico status;
}

