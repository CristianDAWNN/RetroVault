package com.retrovault.retrovault.config;

import com.retrovault.retrovault.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        Authentication authentication) throws ServletException, IOException {
        
        // Extrae el nombre del usuario que acaba de iniciar sesión
        String username = authentication.getName();
        
        // Llama al servicio para actualizar la fecha y hora de su última conexión (last_login)
        userService.updateLastLogin(username);
        
        // Llama al método original para continuar con el flujo normal de redirección después del login
        super.onAuthenticationSuccess(request, response, authentication);
    }
}