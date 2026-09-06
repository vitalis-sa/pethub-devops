package fiap.pethub.security;

import fiap.pethub.enums.Perfil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * Emite e confere os tokens JWT que identificam quem chama a API.
 *
 * O segredo vem de configuração e nunca do código-fonte: quem tem o segredo
 * consegue forjar qualquer identidade.
 */
@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private static final String CLAIM_PERFIL = "perfil";

    private final SecretKey chave;
    private final Duration validade;

    public JwtService(
            @Value("${pethub.jwt.segredo}") String segredo,
            @Value("${pethub.jwt.validade:PT8H}") Duration validade) {
        this.chave = io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                segredo.getBytes(StandardCharsets.UTF_8));
        this.validade = validade;
    }

    public String gerarToken(String email, Perfil perfil) {
        Instant agora = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim(CLAIM_PERFIL, perfil.name())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(validade)))
                .signWith(chave)
                .compact();
    }

    public String extrairEmail(String token) {
        return claims(token).getSubject();
    }

    public Perfil extrairPerfil(String token) {
        return Perfil.valueOf(claims(token).get(CLAIM_PERFIL, String.class));
    }

    /**
     * Um token só é válido se a assinatura confere com a nossa chave e o prazo
     * ainda não venceu. Qualquer outra situação é tratada como inválida — não
     * cabe à API distinguir, para quem chama, entre token forjado e vencido.
     */
    public boolean valido(String token) {
        try {
            claims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token recusado: {}", e.getMessage());
            return false;
        }
    }

    private Claims claims(String token) {
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
