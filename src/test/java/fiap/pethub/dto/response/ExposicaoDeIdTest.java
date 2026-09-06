package fiap.pethub.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fiap.pethub.entity.Exame;
import fiap.pethub.mapper.ExameMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


import static org.assertj.core.api.Assertions.assertThat;

/**
 * Sem o id na resposta, quem consome a API cria um recurso, recebe 201 e fica
 * sem saber o identificador do que acabou de criar — nem a listagem devolve.
 * Na prática, o recurso vira somente-escrita: não há como montar a URL de
 * GET, PUT ou DELETE.
 */
class ExposicaoDeIdTest {

    private final ObjectMapper json = new ObjectMapper();

    @ParameterizedTest(name = "{0} expõe o campo id")
    @ValueSource(classes = {
            ExameResponse.class,
            DiagnosticoResponse.class,
            PedidoMedicoResponse.class,
            VacinaTratamentoResponse.class,
            LeituraWearableResponse.class
    })
    void respostaDeveExporCampoId(Class<?> tipoDaResposta) throws Exception {
        Object resposta = tipoDaResposta.getDeclaredConstructor().newInstance();

        JsonNode corpo = json.valueToTree(resposta);

        assertThat(corpo.has("id"))
                .as("%s precisa expor 'id', senão o recurso não pode ser lido, "
                        + "atualizado nem removido pela API", tipoDaResposta.getSimpleName())
                .isTrue();
    }

    @Test
    void mapperDeveCopiarIdDaEntidadeParaAResposta() {
        Exame exame = Exame.builder()
                .id(42L)
                .tipo("Hemograma")
                .build();

        JsonNode corpo = json.valueToTree(new ExameMapperImpl().toResponse(exame));

        assertThat(corpo.path("id").asLong()).isEqualTo(42L);
    }
}
