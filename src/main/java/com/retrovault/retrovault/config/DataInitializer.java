package com.retrovault.retrovault.config;

import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// @Component hace que Spring maneje esta clase
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Método que se ejecuta justo después de que la aplicación termine de cargar
    @Override
    public void run(String... args) throws Exception {
        // Comprueba si el admin existe en la base de datos
        if (userRepository.findByUsername("admin") == null) {
            
            // Si no existe, lo crea con un nombre de usuario, contraseña y rol de administrador
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@retrovault.com");
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }
    }
}