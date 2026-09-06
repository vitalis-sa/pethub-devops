package fiap.pethub.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * O Spring Boot não lê arquivos .env por conta própria. Sem este carregador, a
 * aplicação só sobe pelo run.ps1 — pela IDE, por mvn spring-boot:run puro ou
 * pelo jar empacotado, as variáveis não existem e o boot morre resolvendo
 * ${JWT_SECRET}.
 */
class ConfiguracaoDotenvTest {

    private final ConfiguracaoDotenv carregador = new ConfiguracaoDotenv();

    private Path escreverEnv(Path pasta, String conteudo) throws IOException {
        Path arquivo = pasta.resolve(".env");
        Files.writeString(arquivo, conteudo);
        return arquivo;
    }

    @Test
    void carregaVariavelDoArquivo(@TempDir Path pasta) throws IOException {
        Path env = escreverEnv(pasta, "JWT_SECRET=segredo-do-arquivo\n");
        var environment = new StandardEnvironment();

        carregador.carregar(environment, env);

        assertThat(environment.getProperty("JWT_SECRET")).isEqualTo("segredo-do-arquivo");
    }

    @Test
    void ignoraComentariosLinhasEmBrancoEEspacos(@TempDir Path pasta) throws IOException {
        Path env = escreverEnv(pasta, """
                # comentario no topo

                DB_USER = rm000000

                # outro comentario
                DB_PASSWORD=senha
                """);
        var environment = new StandardEnvironment();

        carregador.carregar(environment, env);

        assertThat(environment.getProperty("DB_USER")).isEqualTo("rm000000");
        assertThat(environment.getProperty("DB_PASSWORD")).isEqualTo("senha");
    }

    /**
     * Em container e em produção o .env não existe e a configuração vem do
     * ambiente. Se o arquivo vencesse, um .env esquecido na máquina
     * sobrescreveria a configuração real — por isso ele entra com a menor
     * precedência.
     */
    @Test
    void ambienteRealVenceOArquivo(@TempDir Path pasta) throws IOException {
        Path env = escreverEnv(pasta, "JWT_SECRET=valor-do-arquivo\n");
        var environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(
                new MapPropertySource("ambiente-real", Map.of("JWT_SECRET", "valor-do-ambiente")));

        carregador.carregar(environment, env);

        assertThat(environment.getProperty("JWT_SECRET")).isEqualTo("valor-do-ambiente");
    }

    @Test
    void semArquivoNaoQuebra(@TempDir Path pasta) {
        var environment = new StandardEnvironment();

        assertThatCode(() -> carregador.carregar(environment, pasta.resolve(".env")))
                .doesNotThrowAnyException();
    }

    @Test
    void valorComSinalDeIgualPermaneceInteiro(@TempDir Path pasta) throws IOException {
        // Segredos em base64 terminam com "=" e a URL do Oracle carrega ":" e "/".
        Path env = escreverEnv(pasta, "JWT_SECRET=abc123==\nDB_URL=jdbc:oracle:thin:@//host:1521/XE\n");
        var environment = new StandardEnvironment();

        carregador.carregar(environment, env);

        assertThat(environment.getProperty("JWT_SECRET")).isEqualTo("abc123==");
        assertThat(environment.getProperty("DB_URL")).isEqualTo("jdbc:oracle:thin:@//host:1521/XE");
    }
}
