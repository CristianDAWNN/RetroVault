package com.retrovault.retrovault.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(String toEmail, String username) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("retrovault.app@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("🚀 ¡Bienvenido a RetroVault!");

            String htmlContent = """
                <body style="background-color: #212529; color: white; font-family: Arial, sans-serif; text-align: center; padding: 40px;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: #343a40; padding: 20px; border-radius: 10px; border: 1px solid #d63384;">
                        
                        <h1 style="color: #fff; text-shadow: 0 0 10px #d63384;">👾 RetroVault</h1>
                        
                        <h2 style="color: #0dcaf0;">¡Hola, %s!</h2>
                        
                        <p style="font-size: 16px; color: #ced4da;">
                            Gracias por unirte a la comunidad definitiva de coleccionistas.
                            Tu cuenta ha sido creada con éxito.
                        </p>
                        
                        <hr style="border-color: #495057; margin: 20px 0;">
                        
                        <p>Ya puedes empezar a subir tus juegos y consolas.</p>
                        
                        <a href="http://localhost:8080/login" style="background-color: #0dcaf0; color: #000; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block; margin-top: 10px;">
                            Ir a Iniciar Sesión
                        </a>
                        
                        <p style="margin-top: 30px; font-size: 12px; color: #6c757d;">
                            Si no te has registrado tú, ignora este mensaje... aunque te perderás mucha diversión.
                        </p>
                    </div>
                </body>
                """.formatted(username);

            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("✅ Correo enviado a " + toEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("❌ Error enviando correo: " + e.getMessage());
        }
    }
}