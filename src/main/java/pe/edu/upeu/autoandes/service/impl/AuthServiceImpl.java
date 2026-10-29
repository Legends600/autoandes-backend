package pe.edu.upeu.autoandes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.upeu.autoandes.config.JwtService;
import pe.edu.upeu.autoandes.dto.LoginRequestDTO;
import pe.edu.upeu.autoandes.dto.LoginResponseDTO;
import pe.edu.upeu.autoandes.dto.UsuarioActualDTO;
import pe.edu.upeu.autoandes.exception.CredencialesInvalidasException;
import pe.edu.upeu.autoandes.security.UsuarioDetailsService;
import pe.edu.upeu.autoandes.security.UsuarioPrincipal;
import pe.edu.upeu.autoandes.service.AuthService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioDetailsService usuarioDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        UsuarioPrincipal usuario;
        try {
            usuario = usuarioDetailsService.loadUserByUsername(request.username().trim());
        } catch (UsernameNotFoundException e) {
            log.warn("Login rechazado: usuario inexistente");
            throw new CredencialesInvalidasException();
        }
        // Usuario inactivo y clave incorrecta responden igual que un usuario inexistente
        if (!usuario.isEnabled() || !passwordEncoder.matches(request.password(), usuario.getPassword())) {
            log.warn("Login rechazado para el usuario {}", usuario.getUsername());
            throw new CredencialesInvalidasException();
        }
        log.info("Login correcto: usuario={}, roles={}", usuario.getUsername(), usuario.getRoles());
        return new LoginResponseDTO(jwtService.generar(usuario), "Bearer", jwtService.getExpiraEnSegundos(),
                usuario.getUsername(), usuario.getNombreCompleto(), usuario.getRoles(), usuario.getPermisos());
    }

    @Override
    public UsuarioActualDTO usuarioActual(String username) {
        UsuarioPrincipal usuario = usuarioDetailsService.loadUserByUsername(username);
        return new UsuarioActualDTO(usuario.getUsername(), usuario.getNombreCompleto(), usuario.getRoles(),
                usuario.getPermisos());
    }
}
