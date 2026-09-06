package fiap.pethub.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Lê o cabeçalho {@code Authorization: Bearer <token>} e, se o token conferir,
 * coloca o usuário no contexto de segurança da requisição.
 *
 * O filtro nunca rejeita a requisição por conta própria: ausência de token apenas
 * deixa o contexto vazio, e quem decide se aquilo é permitido é a cadeia de
 * autorização. Isso mantém as regras de acesso num lugar só.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIXO = "Bearer ";

    private final JwtService jwtService;
    private final UsuarioDetailsService usuarioDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = extrairToken(request);
        if (token != null && jwtService.valido(token)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            autenticar(request, token);
        }
        filterChain.doFilter(request, response);
    }

    private String extrairToken(HttpServletRequest request) {
        String cabecalho = request.getHeader("Authorization");
        return cabecalho != null && cabecalho.startsWith(PREFIXO)
                ? cabecalho.substring(PREFIXO.length())
                : null;
    }

    private void autenticar(HttpServletRequest request, String token) {
        try {
            UserDetails usuario = usuarioDetailsService.loadUserByUsername(jwtService.extrairEmail(token));
            if (!usuario.isEnabled()) {
                return;
            }
            var autenticacao = new UsernamePasswordAuthenticationToken(
                    usuario, null, usuario.getAuthorities());
            autenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(autenticacao);
        } catch (UsernameNotFoundException e) {
            // Token válido de um cadastro que não existe mais. Segue sem autenticar.
            logger.debug("Token íntegro, mas o usuário não existe mais");
        }
    }
}
