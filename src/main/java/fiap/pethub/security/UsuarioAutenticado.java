package fiap.pethub.security;

import fiap.pethub.entity.Responsavel;
import fiap.pethub.entity.Veterinario;
import fiap.pethub.enums.Perfil;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adapta veterinário e responsável ao contrato que o Spring Security espera.
 *
 * Carrega o id além do email porque os endpoints precisam saber de quem é a
 * requisição, não apenas que ela está autenticada.
 */
@Getter
public class UsuarioAutenticado implements UserDetails {

    private final Long id;
    private final String nome;
    private final String email;
    private final String senha;
    private final Perfil perfil;
    private final boolean ativo;

    private UsuarioAutenticado(Long id, String nome, String email, String senha,
                               Perfil perfil, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfil = perfil;
        this.ativo = ativo == null || ativo;
    }

    public static UsuarioAutenticado de(Veterinario veterinario) {
        return new UsuarioAutenticado(veterinario.getId(), veterinario.getNome(),
                veterinario.getEmail(), veterinario.getSenha(),
                Perfil.VETERINARIO, veterinario.getAtivo());
    }

    public static UsuarioAutenticado de(Responsavel responsavel) {
        return new UsuarioAutenticado(responsavel.getId(), responsavel.getNome(),
                responsavel.getEmail(), responsavel.getSenha(),
                Perfil.RESPONSAVEL, responsavel.getAtivo());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(perfil.authority()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    /** Desligar um veterinário ou desativar um tutor tira o acesso imediatamente. */
    @Override
    public boolean isEnabled() {
        return ativo;
    }
}
