package fiap.pethub.service;

import fiap.pethub.dto.request.LoginRequest;
import fiap.pethub.dto.request.ResponsavelRequest;
import fiap.pethub.dto.request.VeterinarioRequest;
import fiap.pethub.dto.response.LoginResponse;
import fiap.pethub.entity.Responsavel;
import fiap.pethub.entity.Veterinario;
import fiap.pethub.enums.Perfil;
import fiap.pethub.exception.EmailJaCadastradoException;
import fiap.pethub.repository.ResponsavelRepository;
import fiap.pethub.repository.VeterinarioRepository;
import fiap.pethub.security.JwtService;
import fiap.pethub.security.UsuarioAutenticado;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * O perfil de um usuário vem de qual cadastro contém o email dele. Se o mesmo
 * email existisse nos dois cadastros, o perfil — e portanto a permissão — ficaria
 * indefinido. Impedir isso no registro é o que sustenta a autorização inteira.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;
    @Mock private VeterinarioRepository veterinarioRepository;
    @Mock private ResponsavelRepository responsavelRepository;
    @Mock private VeterinarioService veterinarioService;
    @Mock private ResponsavelService responsavelService;

    private AuthService service() {
        return new AuthService(authenticationManager, jwtService, veterinarioRepository,
                responsavelRepository, veterinarioService, responsavelService);
    }

    @Test
    void naoRegistraResponsavelComEmailJaUsadoPorVeterinario() {
        lenient().when(veterinarioRepository.findByEmail("ana@pethub.com"))
                .thenReturn(Optional.of(Veterinario.builder().id(1L).email("ana@pethub.com").build()));
        var pedido = ResponsavelRequest.builder()
                .nome("Ana").cpf("12345678901").email("ana@pethub.com").senha("senha123").build();

        assertThatThrownBy(() -> service().registrarResponsavel(pedido))
                .isInstanceOf(EmailJaCadastradoException.class);

        verify(responsavelService, never()).create(any());
    }

    @Test
    void naoRegistraVeterinarioComEmailJaUsadoPorResponsavel() {
        lenient().when(veterinarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        lenient().when(responsavelRepository.findByEmail("joao@pethub.com"))
                .thenReturn(Optional.of(Responsavel.builder().id(7L).email("joao@pethub.com").build()));
        var pedido = new VeterinarioRequest("João", "CRMV1", "Clínica geral",
                "joao@pethub.com", "11999999999", "senha123", true, null);

        assertThatThrownBy(() -> service().registrarVeterinario(pedido))
                .isInstanceOf(EmailJaCadastradoException.class);

        verify(veterinarioService, never()).create(any());
    }

    @Test
    void loginDevolveTokenComOPerfilDeQuemAutenticou() {
        var autenticado = UsuarioAutenticado.de(Veterinario.builder()
                .id(1L).nome("Dra. Ana").email("ana@pethub.com").senha("hash").ativo(true).build());
        Authentication resultado = new org.springframework.security.authentication
                .UsernamePasswordAuthenticationToken(autenticado, null, autenticado.getAuthorities());
        lenient().when(authenticationManager.authenticate(any())).thenReturn(resultado);
        lenient().when(jwtService.gerarToken("ana@pethub.com", Perfil.VETERINARIO)).thenReturn("token-abc");

        LoginResponse resposta = service().autenticar(new LoginRequest("ana@pethub.com", "senha123"));

        assertThat(resposta.getToken()).isEqualTo("token-abc");
        assertThat(resposta.getPerfil()).isEqualTo(Perfil.VETERINARIO);
        assertThat(resposta.getNome()).isEqualTo("Dra. Ana");
        assertThat(resposta.getId()).isEqualTo(1L);
    }
}
