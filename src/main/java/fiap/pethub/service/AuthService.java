package fiap.pethub.service;

import fiap.pethub.dto.request.LoginRequest;
import fiap.pethub.dto.request.ResponsavelRequest;
import fiap.pethub.dto.request.VeterinarioRequest;
import fiap.pethub.dto.response.LoginResponse;
import fiap.pethub.dto.response.ResponsavelResponse;
import fiap.pethub.dto.response.VeterinarioResponse;
import fiap.pethub.enums.Perfil;
import fiap.pethub.exception.EmailJaCadastradoException;
import fiap.pethub.repository.ResponsavelRepository;
import fiap.pethub.repository.VeterinarioRepository;
import fiap.pethub.security.JwtService;
import fiap.pethub.security.UsuarioAutenticado;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Autenticação e auto-cadastro.
 *
 * O registro garante a invariante que sustenta toda a autorização: um email
 * existe em um único cadastro. Fosse possível repetir, o perfil do usuário
 * dependeria da ordem da busca.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final VeterinarioRepository veterinarioRepository;
    private final ResponsavelRepository responsavelRepository;
    private final VeterinarioService veterinarioService;
    private final ResponsavelService responsavelService;

    public LoginResponse autenticar(LoginRequest request) {
        Authentication autenticacao = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha()));

        UsuarioAutenticado usuario = (UsuarioAutenticado) autenticacao.getPrincipal();
        return montarResposta(usuario.getEmail(), usuario.getPerfil(), usuario.getNome(), usuario.getId());
    }

    @Transactional
    public LoginResponse registrarResponsavel(ResponsavelRequest request) {
        garantirEmailDisponivel(request.getEmail());
        ResponsavelResponse criado = responsavelService.create(request);
        return montarResposta(request.getEmail(), Perfil.RESPONSAVEL, criado.getNome(), criado.getId());
    }

    @Transactional
    public LoginResponse registrarVeterinario(VeterinarioRequest request) {
        garantirEmailDisponivel(request.getEmail());
        VeterinarioResponse criado = veterinarioService.create(request);
        return montarResposta(request.getEmail(), Perfil.VETERINARIO, criado.getNome(), criado.getId());
    }

    /** O email não pode existir em nenhum dos dois cadastros. */
    private void garantirEmailDisponivel(String email) {
        boolean jaExiste = veterinarioRepository.findByEmail(email).isPresent()
                || responsavelRepository.findByEmail(email).isPresent();
        if (jaExiste) {
            throw new EmailJaCadastradoException(email);
        }
    }

    private LoginResponse montarResposta(String email, Perfil perfil, String nome, Long id) {
        return LoginResponse.builder()
                .token(jwtService.gerarToken(email, perfil))
                .perfil(perfil)
                .nome(nome)
                .id(id)
                .build();
    }
}
