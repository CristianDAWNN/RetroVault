package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.Game;
import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.repository.GameRepository;
import com.retrovault.retrovault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home(Model model) {

        // Totales users y games
        long totalGames = gameRepository.count();
        long totalUsers = userRepository.count();

        // Ranking usuarios por XP
        List<User> topPlayers = userRepository.findAll(Sort.by(Sort.Direction.DESC, "level", "experience"))
                                              .stream()
                                              .limit(3)
                                              .collect(Collectors.toList());

        // Top 3 Juegos mejor valorados
        List<Game> topGames = gameRepository
                .findTop3ByCoverImgNotNullOrderByRateDesc();

        // Últimos juegos añadidos
        List<Game> latestGames = gameRepository
                .findTop6ByCoverImgNotNullOrderByCreatedAtDesc();

        // Top 5 géneros más jugados
        List<Object[]> topGenres = gameRepository
                .findTopGenres(PageRequest.of(0, 5));

        model.addAttribute("title", "Inicio");
        
        // Datos para el Podio
        model.addAttribute("totalGames", totalGames);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("topPlayers", topPlayers);

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