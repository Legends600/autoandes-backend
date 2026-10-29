package pe.edu.upeu.autoandes.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.autoandes.entity.Rol;
import pe.edu.upeu.autoandes.entity.Usuario;
import pe.edu.upeu.autoandes.repository.UsuarioRepository;

import java.util.List;

/** Carga el usuario desde Oracle y calcula sus permisos a partir de sus roles. */
@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UsuarioPrincipal loadUserByUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        List<String> roles = usuario.getRoles().stream().map(Rol::getNombre).sorted().toList();
        return new UsuarioPrincipal(usuario.getUsername(), usuario.getPassword(), usuario.getNombreCompleto(),
                usuario.getActivo(), roles, PermisosPorRol.permisosDe(roles));
    }
}
