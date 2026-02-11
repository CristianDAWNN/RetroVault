package com.retrovault.retrovault.service;

import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void saveUser(User user) {
        if (user.getId() == null || !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        user.setActive(true);
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void updateUserAvatar(Long userId, MultipartFile avatarFile) throws Exception {
        User user = getUserById(userId);
        if (user != null && !avatarFile.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + avatarFile.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/avatars");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(avatarFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            user.setAvatar(fileName);
            userRepository.save(user);
        }
    }

    public void addExperience(User user, int amount){
        int currentXp = user.getExperience() + amount;
        int nextLevelXp = user.getXpToNextLevel();

        while (currentXp >= nextLevelXp) {
            currentXp -= nextLevelXp;
            user.setLevel(user.getLevel() + 1);
            nextLevelXp = user.getXpToNextLevel();
        }
        user.setExperience(currentXp);
        userRepository.save(user);
    }

    public void followUser(String myUsername, Long userIdToFollow) {
        User me = userRepository.findByUsername(myUsername);
        User target = userRepository.findById(userIdToFollow).orElse(null);

        if (me != null && target != null && !me.getId().equals(target.getId())) {
            me.follow(target);
            userRepository.save(me);
        }
    }

    public void unfollowUser(String myUsername, Long userIdToUnfollow) {
        User me = userRepository.findByUsername(myUsername);
        User target = userRepository.findById(userIdToUnfollow).orElse(null);

        if (me != null && target != null) {
            me.unfollow(target);
            userRepository.save(me);
        }
    }

    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        }
    }

    public void updateLastLogin(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    public void updateUserFromAdmin(User user) {
        User existingUser = userRepository.findById(user.getId()).orElse(null);
        
        if (existingUser != null) {
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            existingUser.setRole(user.getRole());
            existingUser.setActive(user.isActive());            
            userRepository.save(existingUser);
        }
    }
}