package com.retrovault.retrovault.service;

import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Recuperamos la contraseña tal cual está en la base de datos
        String passwordActual = user.getPassword();

        // LÓGICA INTELIGENTE:
        // Si NO empieza por {noop}, se lo añadimos (para compatibilidad con usuarios viejos Gamer1/Gamer2)
        // Si YA empieza por {noop} (usuarios nuevos registrados), la usamos tal cual.
        if (!passwordActual.startsWith("{noop}")) {
            passwordActual = "{noop}" + passwordActual;
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(passwordActual) // Usamos la variable corregida
                .roles("USER")
                .build();
    }
}