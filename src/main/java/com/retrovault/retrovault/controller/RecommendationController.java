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

    // API KEY de Gemini
    private final String API_KEY = "AIzaSyDdiF9u96wXVTLX8NnYG1MjB3GEMQmUpfY"; 
    
    // Ruta del script de pyhthon para recomendaciones IA
    private final String PYTHON_SCRIPT_PATH = "scripts/recommend.py";

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
            
            // Limpieza de datos para enviar solo lo necesario a la interfaz (título, puntuación y consola)
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

    // MODO IA: Integración con Python para obtener sugerencias de la ia
    @GetMapping("/api/recommendations/ai")
    @ResponseBody
    public ResponseEntity<String> getAiRecommendations(@RequestParam String genre) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", PYTHON_SCRIPT_PATH, API_KEY, genre);
            Process process = processBuilder.start();

            // Captura de la salida estándar del script de Python
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            // Gestión del código de salida para asegurar que el script finalizó correctamente
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return ResponseEntity.ok(output.toString());
            } else {
                return ResponseEntity.status(500).body("[{\"error\": \"Fallo en la ejecución del script Python\"}]");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("[{\"error\": \"" + e.getMessage() + "\"}]");
        }
    }
}