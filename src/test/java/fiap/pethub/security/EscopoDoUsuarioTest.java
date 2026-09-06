package fiap.pethub.security;

import fiap.pethub.entity.Responsavel;
import fiap.pethub.entity.Veterinario;
import fiap.pethub.exception.AcessoNegadoException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Este componente é o único ponto do sistema que lê o SecurityContext, e é dele
 * que todo serviço depende para saber o que o usuário atual pode enxergar. Se
 * ele errar, a autorização inteira erra junto.
 */
class EscopoDoUsuarioTest {

    private final EscopoDoUsuario escopo = new EscopoDoUsuario();

    private void autenticar(UsuarioAutenticado usuario) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
    }

    private UsuarioAutenticado veterinario() {
        return UsuarioAutenticado.de(Veterinario.builder()
                .id(1L).nome("Dra. Ana").email("ana@pethub.com").senha("hash").ativo(true).build());
    }

    private UsuarioAutenticado responsavel(Long id) {
        return UsuarioAutenticado.de(Responsavel.builder()
                .id(id).nome("Tutor").email("tutor" + id + "@pethub.com").senha("hash").ativo(true).build());
    }

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void reconheceVeterinario() {
        autenticar(veterinario());

        assertThat(escopo.ehVeterinario()).isTrue();
    }

    @Test
    void reconheceResponsavelEDevolveOIdDele() {
        autenticar(responsavel(7L));

        assertThat(escopo.ehVeterinario()).isFalse();
        assertThat(escopo.idDoResponsavel()).isEqualTo(7L);
    }

    @Test
    void responsavelAcessaOProprioRecurso() {
        autenticar(responsavel(7L));

        assertThatCode(() -> escopo.exigirPosse(7L)).doesNotThrowAnyException();
    }

    @Test
    void responsavelNaoAcessaRecursoDeOutro() {
        autenticar(responsavel(7L));

        assertThatThrownBy(() -> escopo.exigirPosse(99L))
                .isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    void veterinarioAcessaRecursoDeQualquerResponsavel() {
        autenticar(veterinario());

        assertThatCode(() -> escopo.exigirPosse(99L)).doesNotThrowAnyException();
    }

    /**
     * Ler cadastro alheio o veterinário pode — precisa disso para vincular um pet
     * ao tutor. Editar, não: o cadastro é do titular.
     */
    @Test
    void veterinarioNaoEditaCadastroDeOutraPessoa() {
        autenticar(veterinario());

        assertThatThrownBy(() -> escopo.exigirSerOProprio(99L))
                .isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    void responsavelEditaOProprioCadastro() {
        autenticar(responsavel(7L));

        assertThatCode(() -> escopo.exigirSerOProprio(7L)).doesNotThrowAnyException();
    }

    @Test
    void responsavelNaoEditaCadastroDeOutro() {
        autenticar(responsavel(7L));

        assertThatThrownBy(() -> escopo.exigirSerOProprio(99L))
                .isInstanceOf(AcessoNegadoException.class);
    }

    /**
     * Veterinários enxergam o mesmo conjunto, então compartilham a entrada de
     * cache. Cada responsável precisa da sua, senão um receberia o recurso do
     * outro num acerto de cache.
     */
    @Test
    void chaveDeCacheSeparaResponsaveisEUneVeterinarios() {
        autenticar(veterinario());
        String chaveVet = escopo.chaveDeCache();

        autenticar(responsavel(7L));
        String chaveTutor7 = escopo.chaveDeCache();

        autenticar(responsavel(8L));
        String chaveTutor8 = escopo.chaveDeCache();

        assertThat(chaveTutor7).isNotEqualTo(chaveTutor8);
        assertThat(chaveVet).isNotEqualTo(chaveTutor7).isNotEqualTo(chaveTutor8);
    }
}
