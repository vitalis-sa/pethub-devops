package fiap.pethub.dto.request;

import fiap.pethub.enums.TipoVacinaTratamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateVacinaTratamentoRequest {

    @NotNull(message = "Tipo é obrigatório")
    private TipoVacinaTratamento tipo;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Data de aplicação é obrigatória")
    private LocalDate dataAplicacao;

    private LocalDate proximaDose;
    private String dose;
    private String observacoes;

    @NotNull(message = "petId é obrigatório")
    private Long petId;

    @NotNull(message = "veterinarioId é obrigatório")
    private Long veterinarioId;

    private Long consultaId;
}

