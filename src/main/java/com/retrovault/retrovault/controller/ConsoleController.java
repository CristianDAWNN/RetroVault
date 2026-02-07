package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.Console;
import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.service.ConsoleService;
import com.retrovault.retrovault.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Arrays;
import java.util.LinkedHashMap; // Importante para mantener el orden
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/consoles")
public class ConsoleController {

    @Autowired
    private ConsoleService consoleService;

    @Autowired
    private UserService userService;
    private List<String> listaCompanias = Arrays.asList(
        "Nintendo", "Sony", "Microsoft", "Sega", "Atari", 
        "SNK (Neo Geo)", "NEC (PC Engine)", "Commodore", "Valve (Steam)", "PC / Otros"
    );
    private Map<String, List<String>> mapaSistemas = new LinkedHashMap<>();
    public ConsoleController() {
        mapaSistemas.put("Nintendo", Arrays.asList(
            "NES", "Super Nintendo (SNES)", "Nintendo 64", "GameCube", "Wii", "Wii U", "Nintendo Switch",
            "Game Boy", "Game Boy Color", "Game Boy Advance", "Nintendo DS", "Nintendo 3DS"
        ));
        
        mapaSistemas.put("Sony", Arrays.asList(
            "PlayStation 1", "PlayStation 2", "PlayStation 3", "PlayStation 4", "PlayStation 5",
            "PSP", "PS Vita"
        ));
        
        mapaSistemas.put("Microsoft", Arrays.asList(
            "Xbox", "Xbox 360", "Xbox One", "Xbox Series X/S"
        ));
        
        mapaSistemas.put("Sega", Arrays.asList(
            "Master System", "Mega Drive / Genesis", "Sega Saturn", "Dreamcast", "Game Gear"
        ));
        
        mapaSistemas.put("Retro / Clásicas", Arrays.asList(
            "Atari 2600", "Atari 7800", "Neo Geo AES/MVS", "PC Engine / TurboGrafx-16", "Commodore 64", "Amiga 500"
        ));
        
        mapaSistemas.put("Modernas / Portátiles", Arrays.asList(
            "Steam Deck", "PC Gaming", "Analogue Pocket", "Otras"
        ));
    }

    @GetMapping
    public String listConsoles(Model model, Principal principal) {
        String username = principal.getName();
        User currentUser = userService.getUserByUsername(username);
        model.addAttribute("consoles", consoleService.getConsolesByUser(currentUser));
        return "consoles"; 
    }
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("console", new Console());
        model.addAttribute("listaCompanias", listaCompanias);
        model.addAttribute("mapaSistemas", mapaSistemas);
        
        return "form-console";
    }
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Console console = consoleService.getAllConsoles().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);

        model.addAttribute("console", console);
        model.addAttribute("listaCompanias", listaCompanias);
        model.addAttribute("mapaSistemas", mapaSistemas);
        
        return "form-console";
    }

@PostMapping("/save")
    public String saveConsole(@Valid @ModelAttribute Console console, 
                              BindingResult result, 
                              Principal principal,
                              Model model) {

        String username = principal.getName();
        User currentUser = userService.getUserByUsername(username);
        if (console.getId() == null && consoleService.existsByNameAndUser(console.getName(), currentUser)) {
            result.rejectValue("name", "error.console", "¡Ya tienes esta consola en tu colección!");
        }
        if (result.hasErrors()) {
            model.addAttribute("listaCompanias", listaCompanias);
            model.addAttribute("mapaSistemas", mapaSistemas);
            
            return "form-console";
        }
        console.setUser(currentUser);
        
        if (console.getCreatedBy() == null || console.getCreatedBy().isEmpty()) {
             console.setCreatedBy(username);
        }
        
        consoleService.saveConsole(console);
        return "redirect:/consoles";
    }
}