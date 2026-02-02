package com.retrovault.retrovault.service;

import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Importante
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        user.setActive(true);
        user.setCreatedAt(java.time.LocalDateTime.now());
        
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        
        userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void updateUserFromAdmin(User user) {
        User existingUser = userRepository.findById(user.getId()).orElse(null);
        
        if (existingUser != null) {
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            existingUser.setRole(user.getRole());
            
            
            userRepository.save(existingUser);
        }
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}