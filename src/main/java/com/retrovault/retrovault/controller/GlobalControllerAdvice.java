package com.retrovault.retrovault.controller;

import com.retrovault.retrovault.model.User;
import com.retrovault.retrovault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    // Define un atributo global llamado "currentUser" accesible desde cualquier vista
    @ModelAttribute("currentUser")
    public User getCurrentUser() {
        // Obtenemos la información de autenticación actual desde el contexto de seguridad de Spring
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Verificamos que el usuario esté autenticado y que no sea un usuario sin registro
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            // Buscamos y retornamos el objeto Usuario desde la base de datos
            return userService.getUserByUsername(auth.getName());
        }
        
        // Si no hay nadie logueado, el atributo es nulo
        return null;
    }
}