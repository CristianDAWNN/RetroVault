package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, Principal principal) {
        User currentUser = userService.getUserByUsername(principal.getName());
        
        if (currentUser.getId().equals(id)) {
            return "redirect:/admin/users?error=self_delete";
        }

        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/admin/users";
        }
        model.addAttribute("user", user);
        return "admin/edit-user"; 
    }

    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute User user) {
        userService.updateUserFromAdmin(user);
        return "redirect:/admin/users";
    }
}