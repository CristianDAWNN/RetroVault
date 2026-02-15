package com.retrovault.retrovault.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Sobrescribe el registro de manejadores de recursos estáticos
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        
        // Intercepta cualquier petición URL que contenga la ruta /uploads/
        registry.addResourceHandler("/uploads/**")
                // Redirige esa petición para leer el archivo directamente desde la carpeta física uploads
                .addResourceLocations("file:uploads/");
    }
}