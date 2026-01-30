package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.Game;
import com.retrovault.retrovault.service.ConsoleService;
import com.retrovault.retrovault.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;
    
    @Autowired
    private ConsoleService consoleService;

    // LISTAR JUEGOS
    @GetMapping
    public String listGames(Model model) {
        model.addAttribute("games", gameService.getAllGames());
        return "games"; // Vista de la lista
    }

    // FORMULARIO DE AÑADIR
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("game", new Game());
        // Pasamos la lista de consolas a la vista para el <select>
        model.addAttribute("consoles", consoleService.getAllConsoles());
        return "form-game";
    }

    // GUARDAR
    @PostMapping("/save")
    public String saveGame(@ModelAttribute Game game, 
                           @RequestParam("file") MultipartFile file) { // Recibimos el archivo
        
        // Lógica para guardar la imagen
        if (!file.isEmpty()) {
            try {
                // Ruta absoluta donde se guardarán las fotos (carpeta "uploads" en la raíz del proyecto)
                Path directorioImagenes = Paths.get("uploads");
                String rutaAbsoluta = directorioImagenes.toFile().getAbsolutePath();

                // Crear el directorio si no existe
                if (!Files.exists(directorioImagenes)) {
                    Files.createDirectories(directorioImagenes);
                }

                String nombreArchivo = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                
                // Guardar bytes: leemos los bytes del fichero y los escribimos en la ruta
                byte[] bytesImg = file.getBytes();
                Path rutaCompleta = Paths.get(rutaAbsoluta + "/" + nombreArchivo);
                Files.write(rutaCompleta, bytesImg);

                // Guardamos el nombre en el objeto Juego
                game.setImageUrl(nombreArchivo);

            } catch (IOException e) {
                e.printStackTrace(); // Si falla, que nos lo diga por consola
            }
        }

        // Auditoría básica
        if (game.getId() == null) {
            game.setCreatedBy("WebUser");
        }
        
        //Guardar en BBDD
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