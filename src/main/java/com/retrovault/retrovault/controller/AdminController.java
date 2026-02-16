package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

// Controlador encargado de las funciones de gestión para usuarios con rol ADMIN
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    // Obtiene la lista completa de usuarios para mostrarla en el panel de administración
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    // Elimina un usuario por su ID
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, Principal principal) {
        // Obtiene el usuario actual para validar que no se elimine a sí mismo
        User currentUser = userService.getUserByUsername(principal.getName());
        
        // Impide que un administrador elimine su propia cuenta
        if (currentUser.getId().equals(id)) {
            return "redirect:/admin/users?error=self_delete";
        }

        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    // Muestra el formulario de edición con los datos actuales del user seleccionado
    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/admin/users";
        }
        model.addAttribute("user", user);
        return "admin/edit-user"; 
    }

    // Procesa la actualización de los datos del user desde el panel de admin
    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute User user) {
        userService.updateUserFromAdmin(user);
        return "redirect:/admin/users";
    }
}