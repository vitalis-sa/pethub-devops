package fiap.pethub.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsavelEnderecoRequest {

    @NotBlank(message = "logradouro é obrigatório")
    @Schema(description = "Logradouro", example = "Rua das Flores")
    private String logradouro;

    @NotBlank(message = "número é obrigatório")
    @Schema(description = "Número", example = "123")
    private String numero;

    @Schema(description = "Complemento", example = "Apto 42")
    private String complemento;

    @NotBlank(message = "bairro é obrigatório")
    @Schema(description = "Bairro", example = "Centro")
    private String bairro;

    @NotBlank(message = "cidade é obrigatória")
    @Schema(description = "Cidade", example = "São Paulo")
    private String cidade;

    @NotBlank(message = "estado é obrigatório")
    @Size(min = 2, max = 2, message = "estado deve ter 2 caracteres (UF)")
    @Schema(description = "Estado (UF)", example = "SP")
    private String estado;

    @NotBlank(message = "cep é obrigatório")
    @Size(min = 8, max = 8, message = "CEP deve ter 8 dígitos")
    @Schema(description = "CEP (apenas dígitos)", example = "01001000")
    private String cep;

    @Schema(description = "Indica se é o endereço principal", example = "true")
    private Boolean principal;
}

