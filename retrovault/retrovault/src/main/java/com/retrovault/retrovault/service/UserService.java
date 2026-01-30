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
    
    // Aquí en el futuro pondremos el método para REGISTRAR usuarios nuevos ;)
}