package pe.edu.upeu.autoandes.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/** Usuario autenticado: sus autoridades son los permisos, no los nombres de rol. */
@Getter
public class UsuarioPrincipal implements UserDetails {

    private final String username;
    private final String password;
    private final String nombreCompleto;
    private final boolean activo;
    private final List<String> roles;
    private final List<String> permisos;

    public UsuarioPrincipal(String username, String password, String nombreCompleto, boolean activo,
                            List<String> roles, List<String> permisos) {
        this.username = username;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.activo = activo;
        this.roles = roles;
        this.permisos = permisos;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permisos.stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }
}
