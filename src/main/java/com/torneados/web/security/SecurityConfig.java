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
                //
                // 1) RUTAS PÚBLICAS (SIN NECESIDAD DE TOKEN) 
                //    (///      Obs.: NO llevan prefijo "api/v1" porque nunca son controladores @RestController)
                //    Por ejemplo: página principal, recursos estáticos, Swagger, OpenAPI...
                //
                .requestMatchers(
                    "/", 
                    "/public/**", 
                    "/swagger-ui/**", 
                    "/v3/api-docs/**"
                ).permitAll()

                //
                // 2) ENDPOINTS DE AUTENTICACIÓN / OAUTH
                //    Los controladores @RestController con @RequestMapping("/auth")
                //    se servirán como /api/v1/auth/… en tiempo de ejecución.
                //
                //    - /api/v1/auth/redirect      ← Redirige aquí tras login en Google
                //    - /api/v1/auth/token         ← (opcional) si tu front las invoca directamente
                //    - /api/v1/auth/user-info     ← (opcional) devuelve info del usuario “loggeado”
                //
                .requestMatchers(
                    "/api/v1/auth/redirect",
                    "/api/v1/auth/token",
                    "/api/v1/auth/user-info"
                ).permitAll()

                //
                // 3) ENDPOINTS GET “PÚBLICOS” DE TUS CONTROLADORES @RestController
                //    Por ejemplo tu TorneosController, EquiposController, PartidosController… 
                //    Como tú no pones manualmente “/api/v1” en cada @GetMapping, 
                //    Spring MVC ya los expone como /api/v1/torneos, /api/v1/equipos, etc.
                //
                .requestMatchers(HttpMethod.GET,
                    "/api/v1/torneos",      "/api/v1/torneos/**",
                    "/api/v1/partidos",     "/api/v1/partidos/**",
                    "/api/v1/equipos",      "/api/v1/equipos/**",
                    "/api/v1/deportes",     "/api/v1/tipos"
                ).permitAll()

                //
                // 4) ENDPOINTS DE ADMINISTRACIÓN
                //    Si tienes controladores con @RequestMapping("/admin") (que a su vez, por WebConfig, son “/api/v1/admin/**”)
                //
                .requestMatchers("/api/v1/admin/**")
                    .hasAuthority("ROLE_ADMINISTRADOR")

                //
                // 5) CUALQUIER OTRA PETICIÓN A “/api/v1/...”
                //    (POST a crear/modificar torneos, PUT a actualizar equipos, DELETE, etc.)
                //    requerirá un JWT válido (o sesión OAuth2 activa).
                //
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
