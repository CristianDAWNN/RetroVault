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

        // Totales
        long totalGames = gameRepository.count();
        long totalUsers = userRepository.count();

        // Ranking Usuarios (XP)
        List<User> topPlayers = userRepository.findAll(Sort.by(Sort.Direction.DESC, "level", "experience"))
                                              .stream()
                                              .limit(3)
                                              .collect(Collectors.toList());

        // NUEVO: Top 3 Juegos por Media de Valoración
        List<Object[]> communityTopGames = gameRepository.findTopRatedTitles(PageRequest.of(0, 3));

        // Últimos juegos añadidos
        List<Game> latestGames = gameRepository.findTop6ByCoverImgNotNullOrderByCreatedAtDesc();

        // Top 5 géneros
        List<Object[]> topGenres = gameRepository.findTopGenres(PageRequest.of(0, 5));

        model.addAttribute("title", "Inicio");
        
        model.addAttribute("totalGames", totalGames);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("topPlayers", topPlayers);

        model.addAttribute("communityTopGames", communityTopGames);
        
        model.addAttribute("latestGames", latestGames);
        model.addAttribute("topGenres", topGenres);

        return "index";
    }

    @GetMapping("/privacy")
    public String privacy() { return "privacy"; }

    @GetMapping("/terms")
    public String terms() { return "privacy"; }
}