package fiap.pethub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateExameRequest {

    @NotBlank(message = "Tipo é obrigatório")
    private String tipo;

    @NotNull(message = "Data é obrigatória")
    private LocalDate data;

    private String resultado;
    private String arquivoResultado;

    @NotNull(message = "consultaId é obrigatório")
    private Long consultaId;

    @NotNull(message = "petId é obrigatório")
    private Long petId;
}

