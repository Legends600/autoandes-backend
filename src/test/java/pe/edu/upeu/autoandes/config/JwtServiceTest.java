package pe.edu.upeu.autoandes.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import pe.edu.upeu.autoandes.security.UsuarioPrincipal;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRETO = "secreto-de-prueba-con-mas-de-32-bytes-para-hs256";

    private final UsuarioPrincipal asesor = new UsuarioPrincipal("asesor", "hash", "Ana Torres", true,
            List.of("ASESOR"), List.of("ORDEN_LEER", "ORDEN_REGISTRAR"));

    @Test
    void tokenValidoTransportaUsuarioRolesYPermisos() {
        JwtService jwt = new JwtService(SECRETO, 3600);

        Claims claims = jwt.validar(jwt.generar(asesor));

        assertEquals("asesor", claims.getSubject());
        assertEquals(List.of("ASESOR"), claims.get("roles"));
        assertEquals(List.of("ORDEN_LEER", "ORDEN_REGISTRAR"), claims.get("permisos"));
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }

    @Test
    void tokenVencidoLanzaExpiredJwtException() {
        JwtService jwt = new JwtService(SECRETO, -10);

        assertThrows(ExpiredJwtException.class, () -> jwt.validar(jwt.generar(asesor)));
    }

    @Test
    void tokenConElContenidoAlteradoEsRechazado() {
        JwtService jwt = new JwtService(SECRETO, 3600);
        String[] partes = jwt.generar(asesor).split("\\.");
        // Se cambia el cuerpo (payload) pero se conserva la firma original
        String cuerpoAlterado = partes[1].substring(0, 5)
                + (partes[1].charAt(5) == 'A' ? 'B' : 'A') + partes[1].substring(6);

        assertThrows(JwtException.class,
                () -> jwt.validar(partes[0] + "." + cuerpoAlterado + "." + partes[2]));
    }

    @Test
    void tokenFirmadoConOtraClaveEsRechazado() {
        JwtService emisor = new JwtService("otra-clave-distinta-con-mas-de-32-bytes-1234567", 3600);
        JwtService verificador = new JwtService(SECRETO, 3600);

        assertThrows(JwtException.class, () -> verificador.validar(emisor.generar(asesor)));
    }
}
