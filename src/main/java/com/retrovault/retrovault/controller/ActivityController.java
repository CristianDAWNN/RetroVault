package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.Game;
import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.repository.GameRepository;
import com.retrovault.retrovault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest; // Para el límite
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

    @GetMapping("/activity")
    public String showActivityFeed(Model model, 
                                   Principal principal,
                                   @RequestParam(required = false) Long filterUserId) { // Nuevo parámetro opcional
        
        User currentUser = userService.getUserByUsername(principal.getName());
        List<User> following = currentUser.getFollowing();
        List<Game> activities = new ArrayList<>();

        // CONFIGURAMOS EL LÍMITE: Solo los últimos 50 juegos
        Pageable limit = PageRequest.of(0, 50);

        if (!following.isEmpty()) {
            if (filterUserId != null) {
                // CASO 1: Filtrar por un usuario concreto
                User targetUser = userService.getUserById(filterUserId);
                if (following.contains(targetUser)) {
                    activities = gameRepository.findByUserOrderByCreatedAtDesc(targetUser, limit);
                    model.addAttribute("selectedUser", filterUserId); // Para marcar el select
                }
            } else {
                // CASO 2: Ver todos
                activities = gameRepository.findByUserInOrderByCreatedAtDesc(following, limit);
            }
        }

        model.addAttribute("activities", activities);
        model.addAttribute("following", following); // Pasamos la lista para llenar el select
        model.addAttribute("followingCount", following.size());
        
        return "activity";
    }
}