package fiap.pethub.dto.request;

import fiap.pethub.enums.StatusConsulta;
import fiap.pethub.enums.TipoConsulta;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaRequest {

    @NotNull(message = "Data e hora são obrigatórias")
    private LocalDateTime dataHora;

    @NotNull(message = "Tipo é obrigatório")
    private TipoConsulta tipo;

    private String observacoes;

    @NotNull(message = "Status é obrigatório")
    private StatusConsulta status;

    @NotNull(message = "petId é obrigatório")
    private Long petId;

    @NotNull(message = "veterinarioId é obrigatório")
    private Long veterinarioId;

    private Long unidadeId;
}

