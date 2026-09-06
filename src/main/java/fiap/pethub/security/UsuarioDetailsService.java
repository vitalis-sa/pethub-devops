package fiap.pethub.security;

import fiap.pethub.repository.ResponsavelRepository;
import fiap.pethub.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Resolve um email para o usuário correspondente.
 *
 * O PetHub não tem tabela de usuários: veterinário e responsável já guardam
 * email e senha, e o perfil é determinado por qual dos dois cadastros contém o
 * email. O registro garante que um mesmo email não exista nos dois lados, então
 * a ordem da busca não cria ambiguidade.
 */
@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final VeterinarioRepository veterinarioRepository;
    private final ResponsavelRepository responsavelRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return veterinarioRepository.findByEmail(email)
                .map(UsuarioAutenticado::de)
                .or(() -> responsavelRepository.findByEmail(email).map(UsuarioAutenticado::de))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Nenhum cadastro encontrado para o email informado"));
    }
}
