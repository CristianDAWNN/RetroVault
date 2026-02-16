package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.repository.ConsoleRepository;
import com.retrovault.retrovault.repository.GameRepository;
import com.retrovault.retrovault.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private ConsoleRepository consoleRepository;

    // Muestra el perfil personal del usuario autenticado
    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        
        // Obtenemos estadísticas del usuario para el Dashboard de perfil
        long gamesCount = gameRepository.countByCreatedBy(user.getUsername());
        long consolesCount = consoleRepository.countByUser(user);
        
        model.addAttribute("user", user);
        model.addAttribute("gamesCount", gamesCount);
        model.addAttribute("consolesCount", consolesCount);
        
        // Gestión de contadores de la red social
        model.addAttribute("followingCount", user.getFollowing().size());
        model.addAttribute("followersCount", user.getFollowers().size());

        // Al ser el propio perfil, configuramos los permisos de edición
        model.addAttribute("isOwnProfile", true);
        model.addAttribute("isFollowing", false); 
        
        return "profile";
    }

    // Muestra el perfil de otros usuarios permitiendo el seguimiento
    @GetMapping("/profile/{id}")
    public String showPublicProfile(@PathVariable Long id, Model model, Principal principal) {
        User publicUser = userService.getUserById(id);
        if (publicUser == null) {
            return "redirect:/";
        }

        boolean isOwnProfile = false;
        boolean isFollowing = false;

        // Lógica para seguir
        if (principal != null) {
            User currentUser = userService.getUserByUsername(principal.getName());
            
            if (currentUser != null) {
                // Verificamos si el usuario está viendo su propio perfil a través del ID público
                isOwnProfile = currentUser.getId().equals(publicUser.getId());
                
                // Verificamos si ya existe una relación de seguimiento
                if (!isOwnProfile) {
                    isFollowing = currentUser.getFollowing().contains(publicUser);
                }
            }
        }

        model.addAttribute("user", publicUser);
        model.addAttribute("isOwnProfile", isOwnProfile); 
        model.addAttribute("isFollowing", isFollowing);

        return "profile";
    }

    // Procesa la actualización de la imagen de avatar
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("avatar") MultipartFile avatar,
                                Principal principal) {
        try {
            User currentUser = userService.getUserByUsername(principal.getName());
            if (!avatar.isEmpty()) {
                userService.updateUserAvatar(currentUser.getId(), avatar);
            }
        } catch (Exception e) {
            return "redirect:/profile?error";
        }
        return "redirect:/profile?success";
    }

    // Seguir a un nuevo usuario
    @PostMapping("/profile/follow/{id}")
    public String followUser(@PathVariable Long id, Principal principal, HttpServletRequest request) {
        if (principal != null) {
            userService.followUser(principal.getName(), id);
        }
        // Vuelve a la pag anterior
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/profile/" + id);
    }

    // dejar de seguir
    @PostMapping("/profile/unfollow/{id}")
    public String unfollowUser(@PathVariable Long id, Principal principal, HttpServletRequest request) {
        if (principal != null) {
            userService.unfollowUser(principal.getName(), id);
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/profile/" + id);
    }
}