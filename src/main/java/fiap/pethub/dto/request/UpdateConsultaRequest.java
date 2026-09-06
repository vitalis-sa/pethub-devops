package fiap.pethub.dto.request;

import fiap.pethub.enums.StatusConsulta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConsultaRequest {
    private LocalDateTime dataHora;
    private String observacoes;
    private StatusConsulta status;
    private Long unidadeId;
}

