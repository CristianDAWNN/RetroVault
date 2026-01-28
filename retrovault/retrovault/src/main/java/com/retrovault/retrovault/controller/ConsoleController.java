package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.Console;
import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.service.ConsoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/consoles")
public class ConsoleController {

    @Autowired
    private ConsoleService consoleService;

    @GetMapping
    public String listConsoles(Model model) {
        List<Console> list = consoleService.getAllConsoles();
        
        model.addAttribute("consoles", list);
        
        return "consoles"; 
    }

// 1. Mostrar el formulario vacío
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("console", new Console()); // Pasamos un objeto vacío para rellenar
        return "form-console";
    }

    // 2. Recibir los datos del formulario y guardar
    @PostMapping("/save")
    public String saveConsole(Console console) {
        // TRUCO TEMPORAL: Como todavía no tenemos Login,
        // vamos a asignar las consolas manualmente al Usuario con ID 1.
        // Si no hacemos esto, fallará porque la consola necesita un dueño.
        User user = new User();
        user.setId(1L); // Asumimos que el usuario 1 existe (lo creamos antes por SQL)
        
        console.setUser(user);
        console.setCreatedBy("WebUser"); // Auditoría temporal
        
        consoleService.saveConsole(console); // Guardamos en BBDD
        
        return "redirect:/consoles"; // Volvemos a la lista para ver el cambio
    }
}