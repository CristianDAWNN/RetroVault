package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.service.EmailService;
import com.retrovault.retrovault.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute User user, 
                               BindingResult result, 
                               Model model) {
        
        //COMPROBAR NOMBRE DUPLICADO
        User existingUser = userService.getUserByUsername(user.getUsername());
        if (existingUser != null) {
            result.rejectValue("username", "error.user", "Este nombre de usuario ya está en uso.");
        }

        //COMPROBAR MAIL DUPLICADO 
        User existingEmail = userService.getUserByEmail(user.getEmail());
        if (existingEmail != null) {
            result.rejectValue("email", "error.user", "Este correo ya está registrado.");
        }

        // COMPROBAR ERRORES DE VALIDACIÓN
        if (result.hasErrors()) {
            return "register"; 
        }

        // GUARDAR
        try {
            userService.saveUser(user);
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar el usuario.");
            return "register";
        }

        //ENVIAR MAIL DE BIENVENIDA
        try {
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());
                System.out.println("Correo de bienvenida enviado a: " + user.getEmail());
            }
        } catch (Exception e) {
            System.err.println("Error enviando el mail: " + e.getMessage());
        }
        
        return "redirect:/login?success";
    }
}