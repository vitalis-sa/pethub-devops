package fiap.pethub.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsavelEnderecoResponse {

    @Schema(description = "ID do endereço")
    private Long id;

    @Schema(description = "Logradouro", example = "Rua das Flores")
    private String logradouro;

    @Schema(description = "Número", example = "123")
    private String numero;

    @Schema(description = "Complemento", example = "Apto 42")
    private String complemento;

    @Schema(description = "Bairro", example = "Centro")
    private String bairro;

    @Schema(description = "Cidade", example = "São Paulo")
    private String cidade;

    @Schema(description = "Estado (UF)", example = "SP")
    private String estado;

    @Schema(description = "CEP", example = "01001000")
    private String cep;

    @Schema(description = "Indica se é o endereço principal")
    private Boolean principal;
}

