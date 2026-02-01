package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // Mostrar formulario de registro
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Procesar el registro
    @PostMapping("/saveUser")
    public String registerUser(@ModelAttribute User user) {
        // Guardamos el usuario
        userService.saveUser(user);
        // Redirigimos al login para que entre
        return "redirect:/login"; 
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Carga el archivo login.html
    }
}