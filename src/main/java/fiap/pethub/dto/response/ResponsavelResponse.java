package fiap.pethub.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsavelResponse {

    @Schema(description = "ID do responsável")
    private Long id;

    @Schema(description = "Nome do responsável", example = "João Silva")
    private String nome;

    @Schema(description = "CPF do responsável", example = "12345678901")
    private String cpf;

    @Schema(description = "Email do responsável", example = "joao@email.com")
    private String email;

    @Schema(description = "Indica se o responsável está ativo")
    private Boolean ativo;

    @Schema(description = "Data de criação do cadastro")
    private LocalDateTime createdAt;

    @Schema(description = "Endereços do responsável")
    private List<ResponsavelEnderecoResponse> enderecos;

    @Schema(description = "Contatos do responsável")
    private List<ResponsavelContatoResponse> contatos;
}

