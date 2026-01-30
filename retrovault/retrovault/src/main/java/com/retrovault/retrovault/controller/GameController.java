package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.Game;
import com.retrovault.retrovault.service.ConsoleService;
import com.retrovault.retrovault.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String saveGame(Game game) {
        if (game.getId() == null) {
            game.setCreatedBy("WebUser");
        }
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