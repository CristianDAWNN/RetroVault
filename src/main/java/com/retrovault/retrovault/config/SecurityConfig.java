package com.retrovault.retrovault.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // DESACTIVAR CSRF PARA LA RUTA DE LA IA
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/games/api/scan") 
            )
            // CONFIGURACIÓN DE RUTAS
            .authorizeHttpRequests(auth -> auth
                // RUTAS PÚBLICAS SIN LOGIN
                .requestMatchers(
                    "/", 
                    "/login", 
                    "/register", 
                    "/save",
                    "/privacy",
                    "/css/**", 
                    "/ranking",
                    "/js/**", 
                    "/images/**", 
                    "/img/**", 
                    "/uploads/**"
                ).permitAll()
                
                // RUTAS DE ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // EL RESTO REQUIERE LOGIN
                .anyRequest().authenticated()
            )
            // CONFIGURACIÓN DE LOGIN
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(loginSuccessHandler)
                .permitAll()
            )
            // CONFIGURACIÓN DE LOGOUT
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }
}