package pe.edu.upeu.autoandes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.autoandes.dto.LoginRequestDTO;
import pe.edu.upeu.autoandes.dto.LoginResponseDTO;
import pe.edu.upeu.autoandes.dto.UsuarioActualDTO;
import pe.edu.upeu.autoandes.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Login con JWT y usuario actual")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(summary = "Inicia sesión y devuelve el token JWT con los roles y permisos")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    @Operation(summary = "Datos y permisos del usuario del token")
    public UsuarioActualDTO me(Authentication authentication) {
        return authService.usuarioActual(authentication.getName());
    }
}
