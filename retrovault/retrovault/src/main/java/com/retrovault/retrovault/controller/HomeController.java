package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.Game;
import com.retrovault.retrovault.repository.GameRepository;
import com.retrovault.retrovault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home(Model model) {
        long totalGames = gameRepository.count();
        long totalUsers = userRepository.count();
        
        List<Game> latestGames = gameRepository.findTop6ByCoverImgNotNullOrderByCreatedAtDesc();

        model.addAttribute("totalGames", totalGames);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("latestGames", latestGames);

        return "index";
    }
}