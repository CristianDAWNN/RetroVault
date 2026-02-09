package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.Game;
import com.retrovault.retrovault.repository.GameRepository;
import com.retrovault.retrovault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

        // Totales
        long totalGames = gameRepository.count();
        long totalUsers = userRepository.count();

        // Hall of Fame – Top 3 mejor valorados
        List<Game> topGames = gameRepository
                .findTop3ByCoverImgNotNullOrderByRateDesc();

        // Últimos añadidos
        List<Game> latestGames = gameRepository
                .findTop6ByCoverImgNotNullOrderByCreatedAtDesc();

        // Top 5 géneros más jugados
        List<Object[]> topGenres =
                gameRepository.findTopGenres(PageRequest.of(0, 5));

        // Enviamos datos a la vista
        model.addAttribute("totalGames", totalGames);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("topGames", topGames);
        model.addAttribute("latestGames", latestGames);
        model.addAttribute("topGenres", topGenres);

        return "index";
    }

        @GetMapping("/privacy")
        public String privacy() {
        return "privacy";
        }

        @GetMapping("/terms")
        public String terms() {
        return "privacy";
    }
}
