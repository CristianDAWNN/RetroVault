package com.retrovault.retrovault.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

// Configuración de Spring Security para el control de acceso
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // GESTIÓN DE CSRF
            .csrf(csrf -> csrf
                // Desactiva el token CSRF solo para el endpoint de la IA (para las peticiones Fetch desde JS)
                .ignoringRequestMatchers("/games/api/scan") 
            )
            // REGLAS DE AUTORIZACIÓN DE RUTAS
            .authorizeHttpRequests(auth -> auth
                // Array de rutas públicas: accesibles por cualquier usuario
                .requestMatchers(
                    "/", "/login", "/register", "/save", "/privacy", "/ranking",
                    "/css/**", "/js/**", "/images/**", "/img/**", "/uploads/**"
                ).permitAll()
                
                // Rutas exclusivas para cuentas de Administrador
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // Cualquier otra ruta no especificada arriba requerirá autenticación
                .anyRequest().authenticated()
            )
            // CONFIGURACIÓN DEL FORMULARIO DE INICIO DE SESIÓN
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(loginSuccessHandler)
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            // CONFIGURACIÓN DEL CIERRE DE SESIÓN
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/") // Redirige a la página principal después de cerrar sesión
                .permitAll()
            );

        return http.build();
    }
}