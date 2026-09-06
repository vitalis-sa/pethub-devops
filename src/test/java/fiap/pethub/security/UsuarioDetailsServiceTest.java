package fiap.pethub.security;

import fiap.pethub.entity.Responsavel;
import fiap.pethub.entity.Veterinario;
import fiap.pethub.enums.Perfil;
import fiap.pethub.repository.ResponsavelRepository;
import fiap.pethub.repository.VeterinarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;

/**
 * A identidade não vem de uma tabela de usuários: vem das próprias entidades de
 * domínio. Qual repositório respondeu determina o perfil, então a ordem e os
 * casos de borda dessa busca são o coração da autorização.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceTest {

    @Mock private VeterinarioRepository veterinarioRepository;
    @Mock private ResponsavelRepository responsavelRepository;
    @InjectMocks private UsuarioDetailsService service;

    private void semVeterinarios() {
        lenient().when(veterinarioRepository.findByEmail(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
    }

    private void semResponsaveis() {
        lenient().when(responsavelRepository.findByEmail(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
    }

    @Test
    void veterinarioCadastradoAutenticaComPerfilVeterinario() {
        Veterinario vet = Veterinario.builder()
                .id(1L).nome("Dra. Ana").email("ana@pethub.com").senha("hash").ativo(true).build();
        lenient().when(veterinarioRepository.findByEmail("ana@pethub.com")).thenReturn(Optional.of(vet));
        semResponsaveis();

        UsuarioAutenticado usuario = (UsuarioAutenticado) service.loadUserByUsername("ana@pethub.com");

        assertThat(usuario.getPerfil()).isEqualTo(Perfil.VETERINARIO);
        assertThat(usuario.getId()).isEqualTo(1L);
        assertThat(usuario.getUsername()).isEqualTo("ana@pethub.com");
    }

    @Test
    void responsavelCadastradoAutenticaComPerfilResponsavel() {
        semVeterinarios();
        Responsavel tutor = Responsavel.builder()
                .id(7L).nome("João").email("joao@pethub.com").senha("hash").ativo(true).build();
        lenient().when(responsavelRepository.findByEmail("joao@pethub.com")).thenReturn(Optional.of(tutor));

        UsuarioAutenticado usuario = (UsuarioAutenticado) service.loadUserByUsername("joao@pethub.com");

        assertThat(usuario.getPerfil()).isEqualTo(Perfil.RESPONSAVEL);
        assertThat(usuario.getId()).isEqualTo(7L);
    }

    @Test
    void emailDesconhecidoNaoAutentica() {
        semVeterinarios();
        semResponsaveis();

        assertThatThrownBy(() -> service.loadUserByUsername("ninguem@pethub.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void veterinarioInativoNaoAutentica() {
        Veterinario desligado = Veterinario.builder()
                .id(2L).nome("Dr. Antigo").email("antigo@pethub.com").senha("hash").ativo(false).build();
        lenient().when(veterinarioRepository.findByEmail("antigo@pethub.com"))
                .thenReturn(Optional.of(desligado));

        UsuarioAutenticado usuario = (UsuarioAutenticado) service.loadUserByUsername("antigo@pethub.com");

        assertThat(usuario.isEnabled()).isFalse();
    }

    @Test
    void perfilViraAuthorityComPrefixoRole() {
        Veterinario vet = Veterinario.builder()
                .id(1L).nome("Dra. Ana").email("ana@pethub.com").senha("hash").ativo(true).build();
        lenient().when(veterinarioRepository.findByEmail("ana@pethub.com")).thenReturn(Optional.of(vet));

        var usuario = service.loadUserByUsername("ana@pethub.com");

        assertThat(usuario.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_VETERINARIO");
    }
}
