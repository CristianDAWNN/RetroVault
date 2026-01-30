package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.Game;
import com.retrovault.retrovault.model.User; // <--- IMPORTANTE
import com.retrovault.retrovault.service.ConsoleService;
import com.retrovault.retrovault.service.GameService;
import com.retrovault.retrovault.service.UserService; // <--- IMPORTANTE
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.io.IOException;
import java.security.Principal; // <--- IMPORTANTE
import java.util.UUID;

@Controller
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;
    
    @Autowired
    private ConsoleService consoleService;

    @Autowired
    private UserService userService; // <--- INYECTAMOS EL SERVICIO DE USUARIOS

    // LISTAR JUEGOS
@GetMapping
    public String listGames(Model model, Principal principal) { // Añade Principal
        String username = principal.getName();
        model.addAttribute("games", gameService.getGamesByUser(username));
        return "games"; 
    }

    // FORMULARIO DE AÑADIR
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("game", new Game());
        model.addAttribute("consoles", consoleService.getAllConsoles());
        return "form-game";
    }

    // GUARDAR
    @PostMapping("/save")
    public String saveGame(@ModelAttribute Game game, 
                           @RequestParam("file") MultipartFile file,
                           Principal principal) { // <--- AÑADIMOS PRINCIPAL
        
        // Lógica para guardar la imagen (Mantenemos lo que ya funcionaba)
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

                game.setImageUrl(nombreArchivo);

            } catch (IOException e) {
                e.printStackTrace(); 
            }
        }

        // --- ZONA DE SEGURIDAD Y AUDITORÍA ---
        // Recuperamos el usuario real
        String username = principal.getName();
        // (Opcional: Si en el futuro el juego tiene campo "User", aquí lo usaríamos)
        // User currentUser = userService.getUserByUsername(username); 

        // Auditoría básica: Guardamos el nombre del creador real
        if (game.getId() == null) {
            game.setCreatedBy(username); // <--- Usamos el nombre real
        }
        
        // Guardar en BBDD
        gameService.saveGame(game);
        
        return "redirect:/games";
    }

    // FORMULARIO DE EDITAR
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Game game = gameService.getGameById(id);
        model.addAttribute("game", game);
        model.addAttribute("consoles", consoleService.getAllConsoles());
        return "form-game";
    }

    // BORRAR
    @GetMapping("/delete/{id}")
    public String deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return "redirect:/games";
    }
}