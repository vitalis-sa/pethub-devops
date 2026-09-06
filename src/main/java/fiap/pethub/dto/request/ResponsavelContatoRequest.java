package fiap.pethub.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsavelContatoRequest {

    @NotBlank(message = "tipo é obrigatório")
    @Schema(description = "Tipo do contato", example = "CELULAR")
    private String tipo;

    @NotBlank(message = "telefone é obrigatório")
    @Schema(description = "Número de telefone", example = "(11) 99999-9999")
    private String telefone;

    @Schema(description = "Indica se é o contato principal", example = "true")
    private Boolean principal;
}
