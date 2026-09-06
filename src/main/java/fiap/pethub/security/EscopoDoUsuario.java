package fiap.pethub.security;

import fiap.pethub.enums.Perfil;
import fiap.pethub.exception.AcessoNegadoException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Responde o que o usuário autenticado pode enxergar.
 *
 * É o único ponto do sistema que lê o SecurityContext: os serviços dependem
 * deste componente, não do Spring Security, o que mantém a regra de posse
 * testável sem subir contexto web.
 *
 * A posse restringe apenas o RESPONSAVEL. O veterinário é corpo clínico e
 * atende qualquer pet — ver docs/autorizacao-granular-design.md.
 */
@Component
public class EscopoDoUsuario {

    public boolean ehVeterinario() {
        return usuario().getPerfil() == Perfil.VETERINARIO;
    }

    /** Id do responsável autenticado. Só faça esta pergunta quando não for veterinário. */
    public Long idDoResponsavel() {
        UsuarioAutenticado usuario = usuario();
        if (usuario.getPerfil() != Perfil.RESPONSAVEL) {
            throw new IllegalStateException("O usuário autenticado não é um responsável");
        }
        return usuario.getId();
    }

    /**
     * Deixa passar se o recurso pertence a quem está pedindo. Veterinário passa
     * sempre; responsável, apenas quando o dono é ele mesmo.
     */
    public void exigirPosse(Long responsavelDono) {
        if (ehVeterinario()) {
            return;
        }
        if (!idDoResponsavel().equals(responsavelDono)) {
            throw new AcessoNegadoException("Recurso não encontrado");
        }
    }

    /**
     * Mais estrito que {@link #exigirPosse}: nem o veterinário passa.
     *
     * Serve para o cadastro do responsável, que o veterinário precisa **ler**
     * (para vincular um pet ao tutor) mas não deve **editar** — dados pessoais
     * pertencem ao titular.
     */
    public void exigirSerOProprio(Long responsavelDono) {
        if (ehVeterinario() || !idDoResponsavel().equals(responsavelDono)) {
            throw new AcessoNegadoException("Recurso não encontrado");
        }
    }

    /**
     * Compõe a chave de cache junto com o id do recurso.
     *
     * Sem isto, um recurso carregado por um responsável seria devolvido a outro
     * num acerto de cache, porque @Cacheable curto-circuita o método e a
     * verificação de posse nunca rodaria. Veterinários compartilham a mesma
     * entrada porque enxergam o mesmo conjunto.
     */
    public String chaveDeCache() {
        return ehVeterinario() ? "vet" : "resp:" + idDoResponsavel();
    }

    private UsuarioAutenticado usuario() {
        var autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacao == null || !(autenticacao.getPrincipal() instanceof UsuarioAutenticado usuario)) {
            throw new IllegalStateException("Nenhum usuário autenticado no contexto");
        }
        return usuario;
    }
}
