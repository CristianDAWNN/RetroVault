package com.retrovault.retrovault.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                // Permitimos acceso total a los recursos estáticos (css, js) y a la carpeta uploads
                .requestMatchers("/css/**", "/js/**", "/uploads/**").permitAll()
                // Permitimos acceso a la página de registro y al proceso de registrar
                .requestMatchers("/register", "/saveUser").permitAll()
                // Todo lo demás requiere estar logueado
                .anyRequest().authenticated()
            )
            .formLogin((form) -> form
                // Usamos el login por defecto de Spring, pero redirigimos a /games al entrar
                .defaultSuccessUrl("/games", true)
                .permitAll()
            )
            .logout((logout) -> logout
                .logoutUrl("/logout") // URL para salir
                .logoutSuccessUrl("/login?logout") // A dónde ir tras salir
                .permitAll()
            );

        return http.build();
    }
}