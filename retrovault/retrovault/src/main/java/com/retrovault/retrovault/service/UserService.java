package com.retrovault.retrovault.service;

import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
public void saveUser(User user) {
        // Encriptación "básica" para que Spring Security lo acepte
        // (En el futuro aquí usaremos un encriptador real)
        if (!user.getPassword().startsWith("{noop}")) {
            user.setPassword("{noop}" + user.getPassword());
        }
        
        user.setActive(true); // Activamos el usuario por defecto
        user.setCreatedAt(java.time.LocalDateTime.now());
        
        userRepository.save(user);
    }
}