package fiap.pethub.security;

import fiap.pethub.enums.Perfil;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * O token é a única prova de identidade que a API aceita. Se ele puder ser
 * forjado ou usado depois de vencido, a proteção de rotas não vale nada.
 */
class JwtServiceTest {

    private static final String SEGREDO =
            "segredo-de-teste-com-pelo-menos-256-bits-para-hs256-funcionar";

    private final JwtService jwtService = new JwtService(SEGREDO, Duration.ofHours(8));

    @Test
    void tokenDeveCarregarEmailEPerfilDeQuemAutenticou() {
        String token = jwtService.gerarToken("vet@pethub.com", Perfil.VETERINARIO);

        assertThat(jwtService.extrairEmail(token)).isEqualTo("vet@pethub.com");
        assertThat(jwtService.extrairPerfil(token)).isEqualTo(Perfil.VETERINARIO);
    }

    @Test
    void tokenVencidoDeveSerRecusado() {
        JwtService jaVencido = new JwtService(SEGREDO, Duration.ofSeconds(-1));

        String token = jaVencido.gerarToken("vet@pethub.com", Perfil.VETERINARIO);

        assertThat(jwtService.valido(token)).isFalse();
    }

    @Test
    void tokenAssinadoComOutroSegredoDeveSerRecusado() {
        JwtService invasor = new JwtService(
                "outro-segredo-igualmente-longo-para-passar-no-tamanho-minimo", Duration.ofHours(8));

        String tokenForjado = invasor.gerarToken("vet@pethub.com", Perfil.VETERINARIO);

        assertThat(jwtService.valido(tokenForjado)).isFalse();
    }

    @Test
    void tokenIntegroEDentroDaValidadeDeveSerAceito() {
        String token = jwtService.gerarToken("tutor@pethub.com", Perfil.RESPONSAVEL);

        assertThat(jwtService.valido(token)).isTrue();
    }
}
