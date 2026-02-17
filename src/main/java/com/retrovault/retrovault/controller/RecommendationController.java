package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.Game;
import com.retrovault.retrovault.repository.GameRepository;
import com.retrovault.retrovault.service.GeminiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class RecommendationController {

    @Autowired
    private GameRepository gameRepository;

    // Inyectamos nuestro nuevo servicio de IA
    @Autowired
    private GeminiService geminiService;

    // Carga la interfaz de usuario para el buscador de recomendaciones
    @GetMapping("/recommendations")
    public String showRecommendations(Model model) {
        return "recommendations";
    }

    // MODO MANUAL: Consulta interna en la base de datos de RetroVault
    @GetMapping("/api/recommendations/manual")
    @ResponseBody
    public ResponseEntity<?> getManualRecommendations(@RequestParam String genre) {
        try {
            // Recupera los 5 títulos mejor valorados de la comunidad para ese género
            List<Game> games = gameRepository.findTop5ByGenreAndRateIsNotNullOrderByRateDesc(genre);
            
            // Limpieza de datos para enviar solo lo necesario a la interfaz
            List<Map<String, Object>> cleanData = games.stream().map(g -> {
                Map<String, Object> map = new HashMap<>();
                map.put("title", g.getTitle());
                map.put("rate", g.getRate());
                map.put("consoleName", g.getConsole() != null ? g.getConsole().getName() : "Desconocida");
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(cleanData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("[{\"error\": \"Error en la consulta local\"}]");
        }
    }

    // MODO IA
    @GetMapping("/api/recommendations/ai")
    @ResponseBody
    public ResponseEntity<String> getAiRecommendations(@RequestParam String genre) {
        try {
            // Llamamos a la IA pasándole directamente lo que pidió el usuario
            String jsonResponse = geminiService.recommendGames(genre);
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("[{\"error\": \"Error en la IA: " + e.getMessage() + "\"}]");
        }
    }
}