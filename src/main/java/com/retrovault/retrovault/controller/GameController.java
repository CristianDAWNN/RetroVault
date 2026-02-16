package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.Game;
import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.service.ConsoleService;
import com.retrovault.retrovault.service.GameService;
import com.retrovault.retrovault.service.UserService;
import com.retrovault.retrovault.service.GeminiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

// Controlador principal para la gestión de la colección de videojuegos
@Controller
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;
    
    @Autowired
    private ConsoleService consoleService;

    @Autowired
    private UserService userService;

    @Autowired
    private GeminiService geminiService;

    // Lista los juegos del usuario, permitiendo filtrar por palabras nombre, consola o grnero
    @GetMapping
    public String listGames(Model model, Principal principal, @RequestParam(value = "keyword", required = false) String keyword) {
        String username = principal.getName();
        List<Game> list;
        
        if (keyword != null) {
            list = gameService.searchGames(keyword, username);
        } else {
            list = gameService.getGamesByUser(username);
        }
        
        model.addAttribute("games", list);
        model.addAttribute("keyword", keyword);
        return "games"; 
    }

    // Prepara el formulario de creación cargando las plataformas disponibles del jugador
    @GetMapping("/new")
    public String showCreateForm(Model model, Principal principal) {
        model.addAttribute("game", new Game());
        User currentUser = userService.getUserByUsername(principal.getName());
        model.addAttribute("consoles", consoleService.getConsolesByUser(currentUser));
        return "form-game";
    }

    // Endpoint API para el escaneo de portadas mediante ia
    @PostMapping("/api/scan")
    @ResponseBody 
    public ResponseEntity<String> scanCover(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"No has subido ninguna imagen\"}");
        }
        // Envia la imagen a la ia y devuelve json
        String jsonResult = geminiService.analyzeImage(file);
        return ResponseEntity.ok(jsonResult);
    }

    // Procesa el guardado del juego, la subida de imagen y la xp
    @PostMapping("/save")
    public String saveGame(@Valid @ModelAttribute Game game, 
                           BindingResult result, 
                           @RequestParam("file") MultipartFile file,
                           Principal principal,
                           Model model) {
        
        String username = principal.getName();
        User currentUser = userService.getUserByUsername(username);

        // Validación: evita duplicados del mismo juego en la misma consola para un usuario
        if (game.getId() == null && gameService.existsByTitleAndConsole(game.getTitle(), game.getConsole())) {
            result.rejectValue("title", "error.game", "Ya tienes este juego en la plataforma " + game.getConsole().getName());
        }

        if (result.hasErrors()) {
            model.addAttribute("consoles", consoleService.getConsolesByUser(currentUser));
            return "form-game";
        }

        // Gestión de archivos: almacenamiento local de la portada
        if (!file.isEmpty()) {
            try {
                Path directorioImagenes = Paths.get("uploads");
                if (!Files.exists(directorioImagenes)) Files.createDirectories(directorioImagenes);

                // Generación de nombre único mediante UUID para evitar sobreescritura
                String nombreOriginal = file.getOriginalFilename();
                String nombreLimpio = nombreOriginal != null ? nombreOriginal.replaceAll("[^a-zA-Z0-9\\.\\-]", "_") : "unknown.jpg";
                String nombreArchivo = UUID.randomUUID().toString() + "_" + nombreLimpio;
                
                Files.write(directorioImagenes.resolve(nombreArchivo), file.getBytes());
                game.setCoverImg(nombreArchivo);

            } catch (IOException e) {
                e.printStackTrace(); 
            }
        } else if (game.getId() != null) {
            // Si es edición y no hay archivo nuevo, mantiene la imagen existente
            Game existingGame = gameService.getGameById(game.getId());
            if (existingGame != null) game.setCoverImg(existingGame.getCoverImg());
        }

        // Lógica de gamificación: solo suma XP si es juego nuevo
        if (game.getId() == null) {
            game.setCreatedBy(username);
            game.setUser(currentUser);
            userService.addExperience(currentUser, 50);
        } else {
            if (game.getCreatedBy() == null) game.setCreatedBy(username);
            if (game.getUser() == null) game.setUser(currentUser);
        }
        
        gameService.saveGame(game);
        return "redirect:/games";
    }

    // Borrado de juego y descuento de xp
    @GetMapping("/delete/{id}")
    public String deleteGame(@PathVariable Long id, Principal principal) {
        gameService.deleteGame(id);
        User user = userService.getUserByUsername(principal.getName());

        if (user != null) {
            // Resta 50 XP si elimina 1 juego y asegura no bajar de 0
            user.setExperience(Math.max(0, user.getExperience() - 50));
            userService.saveUser(user);
        }
        return "redirect:/games";
    }
}