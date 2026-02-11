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

@Controller
@RequestMapping("/consoles")
public class ConsoleController {

    @Autowired
    private ConsoleService consoleService;

    @Autowired
    private UserService userService;

    // Listas para los desplegables
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
        // Buscamos la consola
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

    @PostMapping("/save")
    public String saveConsole(@Valid @ModelAttribute Console console, 
                              BindingResult result, 
                              Principal principal,
                              Model model) {

        String username = principal.getName();
        User currentUser = userService.getUserByUsername(username);

        //VALIDACIÓN LÓGICA
        String name = console.getName().toLowerCase().trim();
        String maker = console.getCompany().toLowerCase().trim(); 

        // SONY
        if (name.contains("playstation") || name.contains("ps1") || name.contains("ps2") || name.contains("ps3") || name.contains("ps4") || name.contains("ps5") || name.contains("psp") || name.contains("vita")) {
            if (!maker.contains("sony")) {
                // VALIDAMOS SOBRE EL CAMPO "company"
                result.rejectValue("company", "error.console", "¡Sacrilegio! La " + console.getName() + " es de Sony.");
            }
        }
        // NINTENDO
        if (name.contains("nintendo") || name.contains("nes") || name.contains("wii") || name.contains("switch") || name.contains("gameboy") || name.contains("ds") || name.contains("gamecube")) {
            if (!maker.contains("nintendo")) {
                result.rejectValue("company", "error.console", "Imposible. La " + console.getName() + " es legendaria de Nintendo.");
            }
        }
        // SEGA
        if (name.contains("sega") || name.contains("sonic") || name.contains("dreamcast") || name.contains("genesis") || name.contains("mega drive") || name.contains("saturn") || name.contains("game gear") || name.contains("master system")) {
            if (!maker.contains("sega")) {
                result.rejectValue("company", "error.console", "Error histórico: La " + console.getName() + " pertenece a SEGA.");
            }
        }
        // MICROSOFT
        if (name.contains("xbox")) {
            if (!maker.contains("microsoft")) {
                result.rejectValue("company", "error.console", "El Jefe Maestro no aprueba esto. Xbox es de Microsoft.");
            }
        }
        // ATARI
        if (name.contains("atari")) {
             if (!maker.contains("atari")) {
                result.rejectValue("company", "error.console", "Si pone Atari en el nombre... ¡el fabricante es Atari!");
            }
        }

        //VALIDACIÓN DE DUPLICADOS
        if (console.getId() == null && consoleService.existsByNameAndUser(console.getName(), currentUser)) {
            result.rejectValue("name", "error.console", "¡Ya tienes esta consola en tu colección!");
        }

        //SI HAY ERRORES, VOLVEMOS AL FORMULARIO
        if (result.hasErrors()) {
            model.addAttribute("listaCompanias", listaCompanias);
            model.addAttribute("mapaSistemas", mapaSistemas);
            return "form-console";
        }

        //ASIGNAR USUARIO Y GUARDAR
        console.setUser(currentUser);
        
        if (console.getCreatedBy() == null || console.getCreatedBy().isEmpty()) {
             console.setCreatedBy(username);
        }
        
        consoleService.saveConsole(console);
        
        return "redirect:/consoles";
    }

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