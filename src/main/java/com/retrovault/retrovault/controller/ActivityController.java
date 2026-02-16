package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.Game;
import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.repository.GameRepository;
import com.retrovault.retrovault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest; 
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ActivityController {

    @Autowired
    private UserService userService;

    @Autowired
    private GameRepository gameRepository;

    // Gestiona la vista de actividad de los usuarios seguidos
    @GetMapping("/activity")
    public String showActivityFeed(Model model, 
                                   Principal principal,
                                   @RequestParam(required = false) Long filterUserId) { 
        
        // Identifica al usuario actual y obtiene su lista de seguidos
        User currentUser = userService.getUserByUsername(principal.getName());
        List<User> following = currentUser.getFollowing();
        List<Game> activities = new ArrayList<>();

        // Define una paginación para limitar a los últimos 50
        Pageable limit = PageRequest.of(0, 50);

        // Si el usuario sigue a alguien, buscamos las actualizaciones
        if (!following.isEmpty()) {
            if (filterUserId != null) {
                // Filtra la actividad para mostrar solo la de un usuario seleccionado
                User targetUser = userService.getUserById(filterUserId);
                if (following.contains(targetUser)) {
                    activities = gameRepository.findByUserOrderByCreatedAtDesc(targetUser, limit);
                    model.addAttribute("selectedUser", filterUserId); 
                }
            } else {
                // Obtiene la actividad global de todos los usuarios seguidos ordenados por fecha
                activities = gameRepository.findByUserInOrderByCreatedAtDesc(following, limit);
            }
        }

        // Carga los datos necesarios en el modelo para la vista Thymeleaf
        model.addAttribute("activities", activities);
        model.addAttribute("following", following); 
        model.addAttribute("followingCount", following.size());
        
        return "activity";
    }
}