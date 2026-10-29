package pe.edu.upeu.autoandes.dto;

import java.util.List;

/** Respuesta del login. Nunca incluye la contraseña ni su hash. */
public record LoginResponseDTO(
        String token,
        String tipo,
        long expiraEnSegundos,
        String username,
        String nombreCompleto,
        List<String> roles,
        List<String> permisos) {
}
