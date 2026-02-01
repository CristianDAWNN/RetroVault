package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.Game;
import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.service.ConsoleService;
import com.retrovault.retrovault.service.GameService;
import com.retrovault.retrovault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.io.IOException;
import java.security.Principal;
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

    // LISTAR JUEGOS
    @GetMapping
    public String listGames(Model model, Principal principal) {
        String username = principal.getName();
        // Usamos el método que busca por el nombre del creador
        model.addAttribute("games", gameService.getGamesByUser(username));
        return "games"; 
    }

    // FORMULARIO DE AÑADIR (CORREGIDO EL DESPLEGABLE)
    @GetMapping("/new")
    public String showCreateForm(Model model, Principal principal) {
        model.addAttribute("game", new Game());
        
        // --- CORRECCIÓN AQUÍ ---
        // 1. Buscamos al usuario conectado
        String username = principal.getName();
        User currentUser = userService.getUserByUsername(username);
        
        // 2. Pasamos SOLO las consolas de este usuario al desplegable
        model.addAttribute("consoles", consoleService.getConsolesByUser(currentUser));
        
        return "form-game";
    }

    // GUARDAR (Mantenemos la lógica de la imagen y el usuario)
    @PostMapping("/save")
    public String saveGame(@ModelAttribute Game game, 
                           @RequestParam("file") MultipartFile file,
                           Principal principal) {
        
        // Guardado de Imagen
        if (!file.isEmpty()) {
            try {
                Path directorioImagenes = Paths.get("uploads");
                String rutaAbsoluta = directorioImagenes.toFile().getAbsolutePath();

                if (!Files.exists(directorioImagenes)) {
                    Files.createDirectories(directorioImagenes);
                }

                String nombreArchivo = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                byte[] bytesImg = file.getBytes();
                Path rutaCompleta = Paths.get(rutaAbsoluta + "/" + nombreArchivo);
                Files.write(rutaCompleta, bytesImg);

                // IMPORTANTE: Usamos setCoverImg (el nombre nuevo)
                game.setCoverImg(nombreArchivo);

            } catch (IOException e) {
                e.printStackTrace(); 
            }
        }

        // Auditoría
        String username = principal.getName();
        if (game.getId() == null) {
            game.setCreatedBy(username);
        } else {
            // Si estamos editando, asegúrate de mantener el creador original o actualizarlo si es necesario
            // De momento lo dejamos simple:
             if (game.getCreatedBy() == null) {
                 game.setCreatedBy(username);
             }
        }
        
        gameService.saveGame(game);
        return "redirect:/games";
    }

    // FORMULARIO DE EDITAR (CORREGIDO EL DESPLEGABLE)
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model, Principal principal) {
        Game game = gameService.getGameById(id);
        model.addAttribute("game", game);
        
        // --- CORRECCIÓN AQUÍ TAMBIÉN ---
        String username = principal.getName();
        User currentUser = userService.getUserByUsername(username);
        model.addAttribute("consoles", consoleService.getConsolesByUser(currentUser));
        
        return "form-game";
    }

    // BORRAR
    @GetMapping("/delete/{id}")
    public String deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return "redirect:/games";
    }
}