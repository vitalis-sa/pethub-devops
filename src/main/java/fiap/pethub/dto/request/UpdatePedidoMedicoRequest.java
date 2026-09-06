package fiap.pethub.dto.request;

import fiap.pethub.enums.StatusPedidoMedico;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePedidoMedicoRequest {
    private String instrucoes;
    private StatusPedidoMedico status;
}

