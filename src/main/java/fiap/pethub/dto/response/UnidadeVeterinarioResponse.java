package fiap.pethub.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnidadeVeterinarioResponse {

    @Schema(description = "ID da unidade", example = "1")
    private Long id;

    @Schema(description = "Nome da unidade/clínica", example = "Clínica PetVida")
    private String nome;

    @Schema(description = "Nomes dos veterinários vinculados")
    private List<String> nomesVeterinarios;

    @Schema(description = "Logradouro", example = "Rua das Flores")
    private String logradouro;

    @Schema(description = "Número", example = "123")
    private String numero;

    @Schema(description = "Bairro", example = "Centro")
    private String bairro;

    @Schema(description = "Cidade", example = "São Paulo")
    private String cidade;

    @Schema(description = "Estado (UF)", example = "SP")
    private String estado;

    @Schema(description = "CEP", example = "01310100")
    private String cep;
}

