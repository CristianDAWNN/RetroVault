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

// Controlador que gestiona el login y sign
@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    // Muestra la vista de inicio de sesión
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Inicializa un objeto Usuario vacío para vincularlo al formulario de registro
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Procesa los datos del formulario de registro con validaciones de seguridad
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute User user, 
                               BindingResult result, 
                               Model model) {
        
        // Verifica si el nombre de usuario ya existe para evitar duplicados
        User existingUser = userService.getUserByUsername(user.getUsername());
        if (existingUser != null) {
            result.rejectValue("username", "error.user", "Este nombre de usuario ya está en uso.");
        }

        // Verifica si el correo electrónico ya está registrado en el sistema
        User existingEmail = userService.getUserByEmail(user.getEmail());
        if (existingEmail != null) {
            result.rejectValue("email", "error.user", "Este correo ya está registrado.");
        }

        // Si hay errores de validación (campos vacíos, formato de mail, duplicados), recarga el formulario
        if (result.hasErrors()) {
            return "register"; 
        }

        // Intenta guardar el nuevo usuario en la base de datos
        try {
            userService.saveUser(user);
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar el usuario.");
            return "register";
        }

        // Intenta enviar un mail de bienvenida (si falla no afecta al registro)
        try {
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());
            }
        } catch (Exception e) {
            // Error controlado: el registro es válido aunque falle el envío del mail
        }
        
        // Redirige al login confirmando el éxito de la operación
        return "redirect:/login?success";
    }
}