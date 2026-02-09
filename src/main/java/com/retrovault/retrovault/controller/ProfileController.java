package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.repository.ConsoleRepository;
import com.retrovault.retrovault.repository.GameRepository;
import com.retrovault.retrovault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private ConsoleRepository consoleRepository;

    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        
        long gamesCount = gameRepository.countByCreatedBy(user.getUsername());
        
        long consolesCount = consoleRepository.countByUser(user);
        
        model.addAttribute("user", user);
        model.addAttribute("gamesCount", gamesCount);
        model.addAttribute("consolesCount", consolesCount);
        
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("avatar") MultipartFile avatar,
                                Principal principal) {
        try {
            User currentUser = userService.getUserByUsername(principal.getName());
            if (!avatar.isEmpty()) {
                userService.updateUserAvatar(currentUser.getId(), avatar);
            }
        } catch (Exception e) {
            return "redirect:/profile?error";
        }
        return "redirect:/profile?success";
    }
}