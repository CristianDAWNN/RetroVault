package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.Game;
import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.service.ConsoleService;
import com.retrovault.retrovault.service.GameService;
import com.retrovault.retrovault.service.UserService;
import com.retrovault.retrovault.service.GeminiService; // Importante

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

    @GetMapping("/new")
    public String showCreateForm(Model model, Principal principal) {
        model.addAttribute("game", new Game());
        String username = principal.getName();
        User currentUser = userService.getUserByUsername(username);
        model.addAttribute("consoles", consoleService.getConsolesByUser(currentUser));
        
        return "form-game";
    }

    // --- ENDPOINT PARA LA IA (PYTHON) ---
    @PostMapping("/api/scan")
    @ResponseBody 
    public ResponseEntity<String> scanCover(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"No has subido ninguna imagen\"}");
        }
        // Llamamos al servicio que ejecuta el script de Python
        String jsonResult = geminiService.analyzeImage(file);
        return ResponseEntity.ok(jsonResult);
    }

    @PostMapping("/save")
    public String saveGame(@Valid @ModelAttribute Game game, 
                           BindingResult result, 
                           @RequestParam("file") MultipartFile file,
                           Principal principal,
                           Model model) {
        
        // Validación de título duplicado
        if (game.getId() == null && gameService.existsByTitleAndConsole(game.getTitle(), game.getConsole())) {
            result.rejectValue("title", "error.game", "Ya tienes este juego en la plataforma " + game.getConsole().getName());
        }

        if (result.hasErrors()) {
            String username = principal.getName();
            User currentUser = userService.getUserByUsername(username);
            model.addAttribute("consoles", consoleService.getConsolesByUser(currentUser));
            return "form-game";
        }

        boolean isNewGame = (game.getId() == null);

        // Lógica de subida de imagen
        if (!file.isEmpty()) {
            try {
                Path directorioImagenes = Paths.get("uploads");
                String rutaAbsoluta = directorioImagenes.toFile().getAbsolutePath();

                if (!Files.exists(directorioImagenes)) {
                    Files.createDirectories(directorioImagenes);
                }

                String nombreOriginal = file.getOriginalFilename();
                String nombreLimpio = nombreOriginal.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
                String nombreArchivo = UUID.randomUUID().toString() + "_" + nombreLimpio;
                
                byte[] bytesImg = file.getBytes();
                Path rutaCompleta = Paths.get(rutaAbsoluta + "/" + nombreArchivo);
                Files.write(rutaCompleta, bytesImg);

                game.setCoverImg(nombreArchivo);

            } catch (IOException e) {
                e.printStackTrace(); 
            }
        } else {
            if (game.getId() != null) {
                Game existingGame = gameService.getGameById(game.getId());
                if (existingGame != null && game.getCoverImg() == null) {
                    game.setCoverImg(existingGame.getCoverImg());
                }
            }
        }

        String username = principal.getName();
        if (game.getId() == null) {
            game.setCreatedBy(username);
        } else if (game.getCreatedBy() == null) {
             game.setCreatedBy(username);
        }
        
        gameService.saveGame(game);

        // Sistema de XP
        if (isNewGame) {
            User currentUser = userService.getUserByUsername(username);
            userService.addExperience(currentUser, 50);
        }

        return "redirect:/games";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model, Principal principal) {
        Game game = gameService.getGameById(id);
        model.addAttribute("game", game);
        String username = principal.getName();
        User currentUser = userService.getUserByUsername(username);
        model.addAttribute("consoles", consoleService.getConsolesByUser(currentUser));
        
        return "form-game";
    }

    @GetMapping("/delete/{id}")
    public String deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return "redirect:/games";
    }
}