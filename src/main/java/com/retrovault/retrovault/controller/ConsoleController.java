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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Controlador encargado de gestionar el catálogo de consolas de los usuarios
@Controller
@RequestMapping("/consoles")
public class ConsoleController {

    @Autowired
    private ConsoleService consoleService;

    @Autowired
    private UserService userService;

    // Listas predefinidas para los selectores de compañías en la vista
    private List<String> listaCompanias = Arrays.asList(
        "Nintendo", "Sony", "Microsoft", "Sega", "Atari", 
        "SNK (Neo Geo)", "NEC (PC Engine)", "Commodore", "Valve (Steam)", "PC / Otros"
    );

    private Map<String, List<String>> mapaSistemas = new LinkedHashMap<>();

    // Constructor que organiza los modelos de consola por su fabricante
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

    // Muestra la colección de consolas del usser
    @GetMapping
    public String listConsoles(Model model, Principal principal) {
        String username = principal.getName();
        User currentUser = userService.getUserByUsername(username);
        model.addAttribute("consoles", consoleService.getConsolesByUser(currentUser));
        return "consoles"; 
    }

    // Prepara el formulario de creación de consolas
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("console", new Console());
        model.addAttribute("listaCompanias", listaCompanias);
        model.addAttribute("mapaSistemas", mapaSistemas);
        return "form-console";
    }

    // Carga los datos de una consola existente para su edición
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Console console = consoleService.getAllConsoles().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (console == null) {
            return "redirect:/consoles";
        }

        model.addAttribute("console", console);
        model.addAttribute("listaCompanias", listaCompanias);
        model.addAttribute("mapaSistemas", mapaSistemas);
        
        return "form-console";
    }

    // Procesa el guardado de la consola con validaciones
    @PostMapping("/save")
    public String saveConsole(@Valid @ModelAttribute Console console, 
                              BindingResult result, 
                              Principal principal,
                              Model model) {

        String username = principal.getName();
        User currentUser = userService.getUserByUsername(username);

        String name = console.getName().toLowerCase().trim();
        String maker = console.getCompany().toLowerCase().trim(); 

        // Comprobaciones para validar que el nombre de la consola coincide con su fabricante
        if (name.contains("playstation") && !maker.contains("sony")) {
            result.rejectValue("company", "error.console", "La consola indicada debe pertenecer a Sony.");
        }
        if ((name.contains("nintendo") || name.contains("nes") || name.contains("wii")) && !maker.contains("nintendo")) {
            result.rejectValue("company", "error.console", "Este sistema es propiedad de Nintendo.");
        }
        if (name.contains("xbox") && !maker.contains("microsoft")) {
            result.rejectValue("company", "error.console", "Xbox es una marca de Microsoft.");
        }

        //Logica para evitar que un usuario agregue dos veces la misma consola
        if (console.getId() == null && consoleService.existsByNameAndUser(console.getName(), currentUser)) {
            result.rejectValue("name", "error.console", "Esta consola ya forma parte de tu colección.");
        }

        // Si existen errores de validación, se recarga el formulario con los avisos correspondientes
        if (result.hasErrors()) {
            model.addAttribute("listaCompanias", listaCompanias);
            model.addAttribute("mapaSistemas", mapaSistemas);
            return "form-console";
        }

        // Vinculación del recurso al usuario y persistencia en la base de datos
        console.setUser(currentUser);
        if (console.getCreatedBy() == null || console.getCreatedBy().isEmpty()) {
             console.setCreatedBy(username);
        }
        
        consoleService.saveConsole(console);
        return "redirect:/consoles";
    }

    // Gestiona la eliminación de una consola
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        if (id > 0) {
            try {
                consoleService.delete(id);
                flash.addFlashAttribute("success", "Consola eliminada correctamente.");
            } catch (Exception e) {
                flash.addFlashAttribute("error", "Error al eliminar la consola.");
            }
        }
        return "redirect:/consoles";
    }
}