package fiap.pethub.dto.response;

import fiap.pethub.enums.Perfil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    @Schema(description = "Token JWT para o cabeçalho Authorization: Bearer")
    private String token;

    @Schema(description = "Perfil do usuário autenticado", example = "VETERINARIO")
    private Perfil perfil;

    @Schema(description = "Nome do usuário autenticado", example = "Dra. Ana")
    private String nome;

    @Schema(description = "Identificador do usuário autenticado", example = "1")
    private Long id;
}
