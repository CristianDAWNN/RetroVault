package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.Game;
import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.repository.GameRepository;
import com.retrovault.retrovault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // Gestiona la página de inicio recopilando métricas generales y contenido destacado
    @GetMapping("/")
    public String home(Model model) {
        // Consultas de conteo total para los indicadores de la landing
        long totalGames = gameRepository.count();
        long totalUsers = userRepository.count();

        // Obtención de los jugadores más activos ordenados por nivel
        List<User> topPlayers = userRepository.findAll(Sort.by(Sort.Direction.DESC, "level", "experience"))
                                             .stream()
                                             .limit(3)
                                             .collect(Collectors.toList());

        // Obtención de juegos mejor valorados y novedades con portadas
        List<Object[]> communityTopGames = gameRepository.findTopRatedTitles(PageRequest.of(0, 3));
        List<Game> latestGames = gameRepository.findTop6ByCoverImgNotNullOrderByCreatedAtDesc();
        List<Object[]> topGenres = gameRepository.findTopGenres(PageRequest.of(0, 5));

        // Inyección de datos en el modelo de la vista
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

    @GetMapping("/ranking")
    public String showRanking(Model model) {
        
        // Recuperación de datos para rankings globales: géneros más populares, usuarios top y consolas más usadas
        List<Object[]> topGenres = gameRepository.findTopGenres(PageRequest.of(0, 10));
        List<User> rankedUsers = userRepository.findAll(
            PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "level", "experience"))
        ).getContent();
        
        Pageable limit = PageRequest.of(0, 10);
        List<Object[]> rankedConsoles = gameRepository.findRankedConsoles(limit);

        
        // Procesamiento de etiquetas y valores para el gráfico de géneros
        List<String> genreLabels = topGenres.stream()
            .map(g -> g[0].toString())
            .collect(Collectors.toList());
            
        List<Long> genreCounts = topGenres.stream()
            .map(g -> (Long) g[1])
            .collect(Collectors.toList());

        // Procesamiento de etiquetas y valores para el gráfico de consolas
        List<String> consoleLabels = rankedConsoles.stream()
            .map(c -> (String) c[0]) 
            .collect(Collectors.toList());
            
        List<Long> consoleCounts = rankedConsoles.stream()
            .map(c -> (Long) c[1])
            .collect(Collectors.toList());

        model.addAttribute("title", "Ranking Global");
        model.addAttribute("topGenres", topGenres);
        model.addAttribute("rankedUsers", rankedUsers);
        model.addAttribute("rankedConsoles", rankedConsoles);
        
        // Envío de listas preparadas para ser consumidas por Chart.js
        model.addAttribute("genreLabels", genreLabels);
        model.addAttribute("genreCounts", genreCounts);
        model.addAttribute("consoleLabels", consoleLabels);
        model.addAttribute("consoleCounts", consoleCounts);
        
        return "ranking";
    }
}