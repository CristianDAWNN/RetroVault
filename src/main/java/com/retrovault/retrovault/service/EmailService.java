package com.retrovault.retrovault.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Servicio encargado de gestionar los correos electrónicos mediante API
@Service
public class EmailService {

    // Obtiene la URL base de la aplicación desde el archivo de configuración
    @Value("${app.base-url:http://localhost:8080}")
    private String appBaseUrl;

    // Aquí inyectamos la clave de Brevo desde Railway
    @Value("${BREVO_API_KEY}")
    private String apiKey;

    @Async // Se envia el correo en segundo plano
    // Método principal para enviar el correo de registro
    public void sendWelcomeEmail(String toEmail, String username) {
        try {
            // Plantilla HTML (Intacta)
            String htmlContent = """
                <body style="background-color: #0f1923; margin: 0; padding: 40px 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;">
                    <table align="center" border="0" cellpadding="0" cellspacing="0" width="100%%" style="max-width: 600px; background-color: #1a2730; border-top: 4px solid #ff4655; border-radius: 4px; padding: 40px 30px; box-shadow: 0 4px 6px rgba(0,0,0,0.3);">
                        <tr>
                            <td align="center" style="padding-bottom: 30px;">
                                <h1 style="color: #ece8e1; margin: 0; font-size: 28px; letter-spacing: 2px; text-transform: uppercase;">RetroVault</h1>
                            </td>
                        </tr>
                        <tr>
                            <td style="color: #ece8e1; font-size: 16px; line-height: 1.6;">
                                <h2 style="color: #ff4655; font-size: 20px; margin-top: 0;">Bienvenido, %s</h2>
                                <p style="margin-bottom: 20px; color: #b1b5c0;">
                                    Tu registro se ha completado con éxito. Ya formas parte de la base de datos de coleccionistas.
                                </p>
                                <p style="margin-bottom: 30px; color: #b1b5c0;">
                                    A partir de este momento puedes comenzar a escanear tus portadas, organizar tu inventario y conectar con otros usuarios de la red.
                                </p>
                            </td>
                        </tr>
                        <tr>
                            <td align="center" style="padding-top: 10px; padding-bottom: 30px;">
                                <a href="%s/login" style="background-color: #ff4655; color: #ffffff; padding: 14px 28px; text-decoration: none; font-weight: bold; font-size: 16px; border-radius: 2px; display: inline-block; text-transform: uppercase; letter-spacing: 1px;">
                                    Acceder al Sistema
                                </a>
                            </td>
                        </tr>
                        <tr>
                            <td align="center" style="border-top: 1px solid #2a3740; padding-top: 20px;">
                                <p style="font-size: 12px; color: #76808c; margin: 0;">
                                    Este es un mensaje automático generado por RetroVault. Por favor, no respondas a este correo.
                                </p>
                            </td>
                        </tr>
                    </table>
                </body>
                """.formatted(username, appBaseUrl);

            // Configuramos la llamada API REST a Brevo
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);
            headers.set("accept", "application/json");

            // Construimos el JSON con los datos del correo
            Map<String, Object> body = new HashMap<>();
            body.put("sender", Map.of("name", "RetroVault", "email", "retrovaultdaw@gmail.com"));
            body.put("to", List.of(Map.of("email", toEmail, "name", username)));
            body.put("subject", "Bienvenido a RetroVault");
            body.put("htmlContent", htmlContent);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            // Disparamos la petición POST
            restTemplate.postForEntity("https://api.brevo.com/v3/smtp/email", request, String.class);
            
            // Log de éxito en la consola para pruebas
            System.out.println("Correo API enviado con éxito a: " + toEmail);

        } catch (Exception e) {
            // Captura silenciosa de errores para no interrumpir el flujo de registro del usuario
            System.err.println("Error interno en la API de correo: " + e.getMessage());
        }
    }
}