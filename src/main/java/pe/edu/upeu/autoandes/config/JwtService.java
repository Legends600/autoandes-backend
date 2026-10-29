package pe.edu.upeu.autoandes.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.edu.upeu.autoandes.security.UsuarioPrincipal;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/** Genera y valida tokens JWT firmados con HS256. */
@Service
public class JwtService {

    private final SecretKey clave;
    private final long expiraEnSegundos;

    public JwtService(@Value("${app.jwt.secret}") String secreto,
                      @Value("${app.jwt.expira-en-segundos}") long expiraEnSegundos) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.expiraEnSegundos = expiraEnSegundos;
    }

    public long getExpiraEnSegundos() {
        return expiraEnSegundos;
    }

    public String generar(UsuarioPrincipal usuario) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(usuario.getUsername())
                .claim("nombreCompleto", usuario.getNombreCompleto())
                .claim("roles", usuario.getRoles())
                .claim("permisos", usuario.getPermisos())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plusSeconds(expiraEnSegundos)))
                .signWith(clave, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Verifica la firma y la expiracion. Lanza ExpiredJwtException si vencio y JwtException
     * si el token esta alterado o mal formado.
     */
    public Claims validar(String token) {
        return Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
    }
}
