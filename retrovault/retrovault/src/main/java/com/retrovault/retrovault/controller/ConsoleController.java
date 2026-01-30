package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.Console;
import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.service.ConsoleService;
import com.retrovault.retrovault.service.UserService; // <--- IMPORTANTE
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal; // <--- IMPORTANTE
import java.util.List;

@Controller
@RequestMapping("/consoles")
public class ConsoleController {

    @Autowired
    private ConsoleService consoleService;

    @Autowired
    private UserService userService; // <--- INYECTAMOS EL SERVICIO DE USUARIOS

@GetMapping
    public String listConsoles(Model model, Principal principal) { // <--- AÑADIR Principal
        
        // 1. Buscamos quién está conectado
        String username = principal.getName();
        User currentUser = userService.getUserByUsername(username);

        // 2. Pedimos SOLO sus consolas (CAMBIO IMPORTANTE)
        // ANTES: List<Console> list = consoleService.getAllConsoles();
        List<Console> list = consoleService.getConsolesByUser(currentUser);
        
        model.addAttribute("consoles", list);
        
        return "consoles"; 
    }

    // Mostrar el formulario vacío
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("console", new Console()); 
        return "form-console";
    }

    // Recibir los datos del formulario y guardar
    @PostMapping("/save")
    public String saveConsole(Console console, Principal principal) { // <--- AÑADIMOS PRINCIPAL
        
        // 1. Recuperamos el nombre del usuario conectado
        String username = principal.getName();
        
        // 2. Buscamos sus datos completos en la base de datos
        User currentUser = userService.getUserByUsername(username);
        
        // 3. Asignamos la consola a ESE usuario (Adiós al hardcodeo)
        console.setUser(currentUser);
        
        // Auditoría
        if (console.getCreatedBy() == null || console.getCreatedBy().isEmpty()) {
             console.setCreatedBy(username);
        }
        
        consoleService.saveConsole(console); // Guardamos en BBDD
        
        return "redirect:/consoles"; 
    }

    @GetMapping("/delete/{id}") 
    public String deleteConsole(@PathVariable Long id) { 
        consoleService.deleteConsole(id); 
        return "redirect:/consoles"; 
    }

    // Método para EDITAR 
    @GetMapping("/edit/{id}") 
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Console console = consoleService.getAllConsoles().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);

        model.addAttribute("console", console);
        return "form-console"; 
    }
}