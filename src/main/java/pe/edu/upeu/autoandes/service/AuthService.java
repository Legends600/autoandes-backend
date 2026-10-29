package pe.edu.upeu.autoandes.service;

import pe.edu.upeu.autoandes.dto.LoginRequestDTO;
import pe.edu.upeu.autoandes.dto.LoginResponseDTO;
import pe.edu.upeu.autoandes.dto.UsuarioActualDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO request);

    UsuarioActualDTO usuarioActual(String username);
}
