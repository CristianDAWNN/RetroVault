package com.retrovault.retrovault.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

// Servicio encargado de gestionar los correo electrónico
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Obtiene la URL base de la aplicación desde el archivo de configuración (application.properties)
    @Value("${app.base-url:http://localhost:8080}")
    private String appBaseUrl;

    @Async //Se envia el correo en segundo plano
    // Método principal para enviar el correo de registro
    public void sendWelcomeEmail(String toEmail, String username) {
        try {
            // Se utiliza MimeMessage para permitir el envío de contenido HTML en lugar de texto plano
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("retrovault.app@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("Bienvenido a RetroVault");

            // Plantilla HTML
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

            // El segundo parámetro en true indica que el contenido debe interpretarse como HTML
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            // Captura silenciosa de errores para no interrumpir el flujo de registro del usuario
            System.err.println("Error interno en el servicio de correo: " + e.getMessage());
        }
    }
}