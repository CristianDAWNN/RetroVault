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
                // AÑADIMOS "/" y "/home" a la lista de permitidos
                .requestMatchers("/", "/home", "/css/**", "/js/**", "/uploads/**").permitAll()
                .requestMatchers("/register", "/saveUser").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin((form) -> form
                .loginPage("/login") // Si no tienes una vista login personalizada, Spring usará la suya, pero esto prepara el terreno
                .defaultSuccessUrl("/games", true) // Al entrar, vamos a mis juegos
                .permitAll()
            )
            .logout((logout) -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/") // Al salir, volvemos a la PORTADA (no al login)
                .permitAll()
            );

        return http.build();
    }
}