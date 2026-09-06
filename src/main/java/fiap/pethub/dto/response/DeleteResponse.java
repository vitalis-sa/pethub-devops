package fiap.pethub.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Resposta de exclusão de recurso")
public class DeleteResponse {

    @Schema(description = "Mensagem de confirmação da exclusão")
    private final String mensagem;

    public static DeleteResponse of(String recurso, Long id) {
        return new DeleteResponse(recurso + " de id " + id + " foi deletado(a) com sucesso");
    }
}

