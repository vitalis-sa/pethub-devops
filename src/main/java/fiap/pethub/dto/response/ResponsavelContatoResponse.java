package fiap.pethub.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsavelContatoResponse {

    @Schema(description = "ID do contato")
    private Long id;

    @Schema(description = "Tipo do contato", example = "CELULAR")
    private String tipo;

    @Schema(description = "Número de telefone", example = "(11) 99999-9999")
    private String telefone;

    @Schema(description = "Indica se é o contato principal")
    private Boolean principal;
}

