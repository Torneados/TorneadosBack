package com.torneados.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                //RUTAS PÚBLICAS (SIN NECESIDAD DE TOKEN) ...
                .requestMatchers(
                    "/", 
                    "/public/**", 
                    "/swagger-ui/**", 
                    "/v3/api-docs/**"
                ).permitAll()
                // ENDPOINTS DE AUTENTICACIÓN / OAUTH2
                .requestMatchers(
                    "/api/v1/auth/redirect",
                    "/api/v1/auth/token",
                    "/api/v1/auth/user-info"
                ).permitAll()
                // ENDPOINTS GET “PÚBLICOS” DE TUS CONTROLADORES @RestController
                .requestMatchers(HttpMethod.GET,
                    "/api/v1/torneos",      "/api/v1/torneos/**",
                    "/api/v1/partidos",     "/api/v1/partidos/**",
                    "/api/v1/equipos",      "/api/v1/equipos/**",
                    "/api/v1/deportes",     "/api/v1/tipos"
                ).permitAll()
                // ENDPOINTS DE ADMINISTRACIÓN
                .requestMatchers("/api/v1/admin/**")
                    .hasAuthority("ROLE_ADMINISTRADOR")
                // CUALQUIER OTRA PETICIÓN A “/api/v1/...”
                .anyRequest().authenticated()
            )
            .exceptionHandling(e -> e
                .accessDeniedHandler((req, res, ex) -> {
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"error\":\"" + ex.getMessage() + "\"}");
                })
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(u -> u
                    .oidcUserService(new org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService())
                )
                .successHandler((req, res, auth) -> {
                    // → Una vez Google complete el login, Spring invocará este successHandler
                    //   y redirigirá aquí, que coincide con tu AuthController.redirectAfterLogin().
                    res.sendRedirect("/api/v1/auth/redirect");
                })
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "JWT_TOKEN")
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
