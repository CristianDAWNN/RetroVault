package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.Game;
import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.repository.GameRepository;
import com.retrovault.retrovault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ActivityController {

    @Autowired
    private UserService userService;

    @Autowired
    private GameRepository gameRepository;

    @GetMapping("/activity")
    public String showActivityFeed(Model model, Principal principal) {
        // Obtenemos al usuario logueado
        User currentUser = userService.getUserByUsername(principal.getName());

        // Obtenemos la lista de usuarios a los que sigue
        List<User> following = currentUser.getFollowing();

        List<Game> activities = new ArrayList<>();

        if (!following.isEmpty()) {
            // Extraemos solo los nombres de usuario
            List<String> followingUsernames = following.stream()
                    .map(User::getUsername)
                    .collect(Collectors.toList());

            // Buscamos los juegos creados por esa gente
            activities = gameRepository.findByCreatedByInOrderByCreatedAtDesc(followingUsernames);
        }

        model.addAttribute("activities", activities);
        model.addAttribute("followingCount", following.size());
        
        return "activity";
    }
}