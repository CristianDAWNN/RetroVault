package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.Game;
import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.service.ConsoleService;
import com.retrovault.retrovault.service.GameService;
import com.retrovault.retrovault.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

    // LISTAR JUEGOS
// Modificamos este método
    @GetMapping
    public String listGames(Model model, Principal principal, @RequestParam(value = "keyword", required = false) String keyword) {
        String username = principal.getName();
        
        List<Game> list;
        
        // Si hay palabra clave, buscamos. Si no, listamos todo.
        if (keyword != null) {
            list = gameService.searchGames(keyword, username);
        } else {
            list = gameService.getGamesByUser(username);
        }
        
        model.addAttribute("games", list);
        model.addAttribute("keyword", keyword); // Para mantener lo escrito en la cajita
        
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
    public String saveGame(@Valid @ModelAttribute Game game, // @Valid activa la revisión
                           BindingResult result,             // Aquí se guardan los errores si los hay
                           @RequestParam("file") MultipartFile file,
                           Principal principal,
                           Model model) { // Necesitamos Model para volver a cargar la lista de consolas si falla
        
        // 1. SI HAY ERRORES DE VALIDACIÓN (Ej: Nota = 20)
        if (result.hasErrors()) {
            // Tenemos que recargar la lista de consolas para que el desplegable no salga vacío
            String username = principal.getName();
            User currentUser = userService.getUserByUsername(username);
            model.addAttribute("consoles", consoleService.getConsolesByUser(currentUser));
            
            // Devolvemos al usuario al formulario (no hacemos redirect, sino return vista)
            return "form-game";
        }

        // 2. SI TODO ESTÁ BIEN, SEGUIMOS CON LA LÓGICA DE SIEMPRE...
        
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

                game.setCoverImg(nombreArchivo);

            } catch (IOException e) {
                e.printStackTrace(); 
            }
        } else {
            // TRUCO: Si estamos editando y no suben foto nueva, mantenemos la vieja
            // (Esto ya lo gestiona el input hidden del html, pero por seguridad)
            if (game.getId() != null) {
                Game existingGame = gameService.getGameById(game.getId());
                if (existingGame != null && game.getCoverImg() == null) {
                    game.setCoverImg(existingGame.getCoverImg());
                }
            }
        }

        // Auditoría
        String username = principal.getName();
        if (game.getId() == null) {
            game.setCreatedBy(username);
        } else if (game.getCreatedBy() == null) {
             game.setCreatedBy(username);
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