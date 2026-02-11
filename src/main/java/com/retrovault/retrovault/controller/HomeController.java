package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.Game;
import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.repository.GameRepository;
import com.retrovault.retrovault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable; // <--- ESTE ERA EL QUE FALTABA
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

        // Ranking Usuarios
        List<User> topPlayers = userRepository.findAll(Sort.by(Sort.Direction.DESC, "level", "experience"))
                                             .stream()
                                             .limit(3)
                                             .collect(Collectors.toList());

        // Top 3 Juegos (Media)
        List<Object[]> communityTopGames = gameRepository.findTopRatedTitles(PageRequest.of(0, 3));

        // Últimos juegos
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

    @GetMapping("/ranking")
    public String showRanking(Model model) {
        
        // OBTENER DATOS
        
        // Top Géneros
        List<Object[]> topGenres = gameRepository.findTopGenres(PageRequest.of(0, 10));
        
        // Top Usuarios
        List<User> rankedUsers = userRepository.findAll(
            PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "level", "experience"))
        ).getContent();
        
        // Top Consolas (Normalizado)
        Pageable limit = PageRequest.of(0, 10);
        List<Object[]> rankedConsoles = gameRepository.findRankedConsoles(limit);

        // PREPARAR LISTAS PARA JAVASCRIPT
        
        // Generos [0]Nombre [1]Cantidad
        List<String> genreLabels = topGenres.stream()
            .map(g -> g[0].toString())
            .collect(Collectors.toList());
            
        List<Long> genreCounts = topGenres.stream()
            .map(g -> (Long) g[1])
            .collect(Collectors.toList());

        // Consolas [0]Nombre [2]Total
        List<String> consoleLabels = rankedConsoles.stream()
            .map(c -> (String) c[0]) 
            .collect(Collectors.toList());
            
        List<Long> consoleCounts = rankedConsoles.stream()
            .map(c -> (Long) c[1])
            .collect(Collectors.toList());

        // ENVIAR AL MODELO
        model.addAttribute("title", "Ranking Global");
        
        // Datos completos para las tablas
        model.addAttribute("topGenres", topGenres);
        model.addAttribute("rankedUsers", rankedUsers);
        model.addAttribute("rankedConsoles", rankedConsoles);
        
        // Listas para los gráficos
        model.addAttribute("genreLabels", genreLabels);
        model.addAttribute("genreCounts", genreCounts);
        model.addAttribute("consoleLabels", consoleLabels);
        model.addAttribute("consoleCounts", consoleCounts);
        
        return "ranking";
    }
}