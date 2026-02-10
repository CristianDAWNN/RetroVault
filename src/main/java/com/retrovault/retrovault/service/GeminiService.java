package com.retrovault.retrovault.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    public String analyzeImage(MultipartFile file) {
        Path tempFile = null;
        try {
            // Crear archivo temporal
            tempFile = Files.createTempFile("upload_", ".jpg");
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            // Ruta al script
            String scriptPath = "scripts/scanner.py";
            
            // Ejecutar Python (Detectando SO)
            String pythonCmd = System.getProperty("os.name").toLowerCase().contains("win") ? "python" : "python3";
            ProcessBuilder pb = new ProcessBuilder(pythonCmd, scriptPath, apiKey, tempFile.toString());
            pb.redirectErrorStream(true); 
            
            Process process = pb.start();

            // Leer respuesta completa
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(" "); // Añadimos espacio por si acaso
            }
            process.waitFor();
            String fullOutput = output.toString();

            // --- AQUÍ ESTÁ LA MAGIA DE LIMPIEZA ---
            // Buscamos dónde empieza '{' y dónde termina '}' para ignorar advertencias previas
            int jsonStart = fullOutput.indexOf("{");
            int jsonEnd = fullOutput.lastIndexOf("}");

            if (jsonStart != -1 && jsonEnd != -1 && jsonStart <= jsonEnd) {
                // Extraemos SOLO el JSON limpio
                return fullOutput.substring(jsonStart, jsonEnd + 1);
            } else {
                // Si no hay JSON, devolvemos el error escapando las barras invertidas de Windows
                String safeOutput = fullOutput.replace("\\", "\\\\").replace("\"", "'");
                return "{\"error\": \"Python no devolvió JSON válido. Salida: " + safeOutput + "\"}";
            }
            // --------------------------------------

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Error interno Java: " + e.getMessage() + "\"}";
        } finally {
            try {
                if (tempFile != null) Files.deleteIfExists(tempFile);
            } catch (Exception ignored) {}
        }
    }
}