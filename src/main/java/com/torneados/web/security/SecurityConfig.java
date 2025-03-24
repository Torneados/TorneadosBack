package com.torneados.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs REST
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/public/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // Rutas públicas
                .requestMatchers("/auth/user-info", "/auth/token", "/torneos").authenticated() // Protegidas
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMINISTRADOR") // Solo admin
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .oidcUserService(new OidcUserService()) // Manejo de usuarios con OpenID Connect
                )
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/") // Redirigir al cerrar sesión
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "JWT_TOKEN") // Eliminar cookies
            )
            // Manejo de excepciones
            .exceptionHandling(e -> e
                // Deja el authenticationEntryPoint por defecto (redirección a Google si NO está autenticado)
                // sobrescribe el accessDeniedHandler para usuarios autenticados sin permisos
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    // El usuario está autenticado, pero no tiene los roles necesarios
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \""+ accessDeniedException.getMessage() +"\"}");
                })
            )
            // Registramos el filtro antes de UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
