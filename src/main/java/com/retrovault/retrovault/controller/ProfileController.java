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

    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        
        long gamesCount = gameRepository.countByCreatedBy(user.getUsername());
        long consolesCount = consoleRepository.countByUser(user);
        
        model.addAttribute("user", user);
        model.addAttribute("gamesCount", gamesCount);
        model.addAttribute("consolesCount", consolesCount);
        
        //Añadimos contadores de follows para la vista del perfil
        model.addAttribute("followingCount", user.getFollowing().size());
        model.addAttribute("followersCount", user.getFollowers().size());

        model.addAttribute("isOwnProfile", true);
        model.addAttribute("isFollowing", false); 
        
        return "profile";
    }

@GetMapping("/profile/{id}")
public String showPublicProfile(@PathVariable Long id, Model model, Principal principal) {
    // 1. Buscamos al usuario dueño del perfil que se va a mostrar
    User publicUser = userService.getUserById(id);
    if (publicUser == null) {
        return "redirect:/";
    }

    // 2. Inicializamos variables por defecto
    boolean isOwnProfile = false;
    boolean isFollowing = false;

    // 3. Lógica para el usuario logueado
    if (principal != null) {
        User currentUser = userService.getUserByUsername(principal.getName());
        
        if (currentUser != null) {
            // Comprobamos si el ID del perfil es el mismo que el del usuario logueado
            isOwnProfile = currentUser.getId().equals(publicUser.getId());
            
            // Comprobamos si ya lo seguimos (solo si no es nuestro propio perfil)
            if (!isOwnProfile) {
                isFollowing = currentUser.getFollowing().contains(publicUser);
            }
        }
    } // <-- Aquí faltaba cerrar el bloque 'if (principal != null)'

    // 4. PASAR DATOS AL MODELO (Esto es lo que soluciona el error 500)
    model.addAttribute("user", publicUser);
    model.addAttribute("isOwnProfile", isOwnProfile); 
    model.addAttribute("isFollowing", isFollowing);

    // Si tienes contadores, añádelos también aquí
    // model.addAttribute("gamesCount", gameRepository.countByCreatedBy(publicUser.getUsername()));

    return "profile";
}

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

    //NUEVOS MÉTODOS DE FOLLOW / UNFOLLOW

    @PostMapping("/profile/follow/{id}")
    public String followUser(@PathVariable Long id, Principal principal, HttpServletRequest request) {
        if (principal != null) {
            userService.followUser(principal.getName(), id);
        }
        // Volvemos al perfil del usuario que acabamos de seguir
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/profile/" + id);
    }

    @PostMapping("/profile/unfollow/{id}")
    public String unfollowUser(@PathVariable Long id, Principal principal, HttpServletRequest request) {
        if (principal != null) {
            userService.unfollowUser(principal.getName(), id);
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/profile/" + id);
    }
}