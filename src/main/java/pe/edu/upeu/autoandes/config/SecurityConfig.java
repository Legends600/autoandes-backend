package pe.edu.upeu.autoandes.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pe.edu.upeu.autoandes.security.RestAccessDeniedHandler;
import pe.edu.upeu.autoandes.security.RestAuthenticationEntryPoint;

import static pe.edu.upeu.autoandes.security.Permiso.*;

/**
 * Seguridad de la API: sin estado, con JWT, y autorizacion por permiso (hasAuthority).
 * La matriz de la seccion 3.2 vive en PermisosPorRol; aqui solo se asocia cada ruta con su permiso.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String API = "/api/v1";

    private final JwtService jwtService;
    private final RestAuthenticationEntryPoint entryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        // Publicos: preflight CORS, login y Swagger
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, API + "/auth/login").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        .requestMatchers(HttpMethod.GET, API + "/auth/me").authenticated()

                        .requestMatchers(HttpMethod.GET, API + "/clientes").hasAuthority(VEHICULO_LEER.name())

                        .requestMatchers(HttpMethod.GET, API + "/servicios", API + "/servicios/**")
                        .hasAuthority(SERVICIO_LEER.name())
                        .requestMatchers(API + "/servicios", API + "/servicios/**")
                        .hasAuthority(SERVICIO_ESCRIBIR.name())

                        .requestMatchers(HttpMethod.GET, API + "/vehiculos", API + "/vehiculos/**")
                        .hasAuthority(VEHICULO_LEER.name())
                        .requestMatchers(API + "/vehiculos", API + "/vehiculos/**")
                        .hasAuthority(VEHICULO_ESCRIBIR.name())

                        .requestMatchers(HttpMethod.POST, API + "/ordenes").hasAuthority(ORDEN_REGISTRAR.name())
                        .requestMatchers(HttpMethod.PATCH, API + "/ordenes/*/anular").hasAuthority(ORDEN_ANULAR.name())
                        .requestMatchers(HttpMethod.GET, API + "/ordenes", API + "/ordenes/**")
                        .hasAuthority(ORDEN_LEER.name())

                        .requestMatchers(API + "/reportes/**").hasAuthority(REPORTE_VER.name())

                        // Cualquier otra ruta exige al menos un token valido
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
