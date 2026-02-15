package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.Game;
import com.retrovault.retrovault.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class RecommendationController {

    @Autowired
    private GameRepository gameRepository;

    // API Key de Gemini
    private final String API_KEY = "AIzaSyDdiF9u96wXVTLX8NnYG1MjB3GEMQmUpfY"; 
    
    // Ruta script de python
    private final String PYTHON_SCRIPT_PATH = "scripts/recommend.py";

    // Mostrar HTML
    @GetMapping("/recommendations")
    public String showRecommendations(Model model) {
        return "recommendations";
    }

    // MODO MANUAL: Busca en la base de datos de la base de datos
    @GetMapping("/api/recommendations/manual")
    @ResponseBody
    public ResponseEntity<?> getManualRecommendations(@RequestParam String genre) {
        try {
            List<Game> games = gameRepository.findTop5ByGenreAndRateIsNotNullOrderByRateDesc(genre);
            
            // Filtramos solo lo que el frontend necesita para evitar la recursión de Jackson (Error 500)
            List<Map<String, Object>> cleanData = games.stream().map(g -> {
                Map<String, Object> map = new HashMap<>();
                map.put("title", g.getTitle());
                map.put("rate", g.getRate());
                map.put("consoleName", g.getConsole() != null ? g.getConsole().getName() : "Desconocida");
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(cleanData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("[{\"error\": \"Error interno del servidor\"}]");
        }
    }

    // MODO IA: Ejecuta Python y devuelve el JSON
    @GetMapping("/api/recommendations/ai")
    @ResponseBody
    public ResponseEntity<String> getAiRecommendations(@RequestParam String genre) {
        try {
            // Ejecutamos: python ai_recommend.py API_KEY GENERO
            ProcessBuilder processBuilder = new ProcessBuilder("python", PYTHON_SCRIPT_PATH, API_KEY, genre);
            Process process = processBuilder.start();

            // Leemos lo que devuelve el print() de Python
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return ResponseEntity.ok(output.toString());
            } else {
                return ResponseEntity.status(500).body("[{\"error\": \"El script de Python falló\"}]");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("[{\"error\": \"" + e.getMessage() + "\"}]");
        }
    }
}