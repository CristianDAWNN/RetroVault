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

        // TRUCO TEMPORAL: Spring Security exige contraseñas encriptadas (con {bcrypt}...).
        // Como nuestras contraseñas en BBDD son texto plano ("1234"), le añadimos {noop}
        // para decirle a Spring: "Oye, no intentes desencriptar esto, léelo tal cual".
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password("{noop}" + user.getPassword()) 
                .roles("USER")
                .build();
    }
}