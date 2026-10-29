package pe.edu.upeu.autoandes.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Valida el token en cada peticion. Si es correcto, deja al usuario autenticado con los permisos
 * que trae el token. Si falta o es invalido, no autentica nada y el punto de entrada responde 401.
 * No se registra como bean para que solo corra dentro de la cadena de Spring Security.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String ATRIBUTO_MOTIVO = "autoandes.motivo401";
    private static final String PREFIJO = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String cabecera = request.getHeader("Authorization");
        if (cabecera != null && cabecera.startsWith(PREFIJO)) {
            try {
                Claims claims = jwtService.validar(cabecera.substring(PREFIJO.length()).trim());
                List<GrantedAuthority> autoridades = permisosDe(claims).stream()
                        .<GrantedAuthority>map(SimpleGrantedAuthority::new).toList();
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(claims.getSubject(), null, autoridades));
            } catch (ExpiredJwtException e) {
                request.setAttribute(ATRIBUTO_MOTIVO, "El token ha vencido; inicie sesión nuevamente");
            } catch (JwtException | IllegalArgumentException e) {
                request.setAttribute(ATRIBUTO_MOTIVO, "El token es inválido");
            }
        }
        chain.doFilter(request, response);
    }

    private List<String> permisosDe(Claims claims) {
        Object permisos = claims.get("permisos");
        if (permisos instanceof List<?> lista) {
            return lista.stream().map(String::valueOf).toList();
        }
        return List.of();
    }
}
