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
        
        //Obtener quién acaba de entrar
        String username = authentication.getName();
        
        // Guardar la fecha en la base de datos
        userService.updateLastLogin(username);
        System.out.println("🕒 Login registrado para: " + username);

        //Continuar con la redirección normal
        super.onAuthenticationSuccess(request, response, authentication);
    }
}