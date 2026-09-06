package fiap.pethub.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Carrega o arquivo {@code .env} da raiz do projeto como fonte de configuração.
 *
 * O Spring Boot não lê arquivos {@code .env} por conta própria. Sem isto, a
 * aplicação só sobe quando alguém exporta as variáveis manualmente antes — o
 * que quebra a execução pela IDE, por {@code mvn spring-boot:run} puro, pelos
 * testes e pelo jar empacotado, todos morrendo ao resolver {@code ${JWT_SECRET}}.
 *
 * Roda como {@link EnvironmentPostProcessor}, antes de qualquer resolução de
 * placeholder, e é registrado em
 * {@code META-INF/spring.factories}.
 */
public class ConfiguracaoDotenv implements EnvironmentPostProcessor {

    private static final String NOME_DA_FONTE = "arquivo .env";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        carregar(environment, Paths.get(".env"));
    }

    /**
     * Adiciona o conteúdo do arquivo com a **menor** precedência do ambiente.
     *
     * Em container e em produção o {@code .env} não existe e a configuração vem
     * das variáveis reais. Se o arquivo vencesse, um {@code .env} esquecido na
     * máquina sobrescreveria silenciosamente a configuração de verdade.
     */
    void carregar(ConfigurableEnvironment environment, Path arquivo) {
        if (!Files.isRegularFile(arquivo)) {
            return;
        }
        Map<String, Object> valores = ler(arquivo);
        if (!valores.isEmpty()) {
            environment.getPropertySources().addLast(new MapPropertySource(NOME_DA_FONTE, valores));
        }
    }

    private Map<String, Object> ler(Path arquivo) {
        Map<String, Object> valores = new HashMap<>();
        try {
            for (String linha : Files.readAllLines(arquivo)) {
                acrescentar(valores, linha.trim());
            }
        } catch (IOException e) {
            // Configuração ilegível não deve derrubar o boot: as variáveis de
            // ambiente reais ainda podem suprir tudo o que a aplicação precisa.
            System.err.println("Não foi possível ler " + arquivo + ": " + e.getMessage());
        }
        return valores;
    }

    private void acrescentar(Map<String, Object> valores, String linha) {
        if (linha.isEmpty() || linha.startsWith("#")) {
            return;
        }
        // Limite de 2: o valor pode conter '=', como num segredo em base64.
        String[] partes = linha.split("=", 2);
        if (partes.length != 2) {
            return;
        }
        String chave = partes[0].trim();
        String valor = partes[1].trim();
        if (!chave.isEmpty() && !valor.isEmpty()) {
            valores.put(chave, valor);
        }
    }
}
