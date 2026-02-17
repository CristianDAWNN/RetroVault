package com.retrovault.retrovault.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

// Servicio encargado de la integración con la Inteligencia Artificial (Google Gemini)
@Service
public class GeminiService {

    // Clave de API inyectada desde el archivo de configuración para mayor seguridad
    @Value("${gemini.api.key}")
    private String apiKey;

    // Método que procesa una imagen mediante un script externo de Python
    public String analyzeImage(MultipartFile file) {
        Path tempFile = null;
        try {
            // Generación de un archivo temporal para que el script de Python pueda acceder a la imagen
            tempFile = Files.createTempFile("upload_", ".jpg");
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            // Localización del script de automatización
            String scriptPath = "scripts/scanner.py";
            
            // Detección dinámica del sistema operativo para invocar el comando de Python correcto
            String pythonCmd = "python3";
            
            // Configuración del proceso externo pasando la API Key y la ruta del archivo temporal
            ProcessBuilder pb = new ProcessBuilder(pythonCmd, scriptPath, apiKey, tempFile.toString());
            pb.redirectErrorStream(true); // Redirige errores a la salida estándar para captura unificada
            
            Process process = pb.start();

            // Lectura de la respuesta generada por el script de Python
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(" ");
            }
            process.waitFor();
            String fullOutput = output.toString();

            // Lógica de saneamiento de respuesta: Extrae exclusivamente el JSON
            int jsonStart = fullOutput.indexOf("{");
            int jsonEnd = fullOutput.lastIndexOf("}");

            if (jsonStart != -1 && jsonEnd != -1 && jsonStart <= jsonEnd) {
                // Retorna únicamente el objeto JSON limpio para ser procesado por el front
                return fullOutput.substring(jsonStart, jsonEnd + 1);
            } else {
                // Gestión de errores en caso de que la IA no devuelva un formato válido
                String safeOutput = fullOutput.replace("\\", "\\\\").replace("\"", "'");
                return "{\"error\": \"Formato JSON no detectado. Salida del sistema: " + safeOutput + "\"}";
            }

        } catch (Exception e) {
            return "{\"error\": \"Excepción en el servicio de análisis: " + e.getMessage() + "\"}";
        } finally {
            // Limpieza del sistema de archivos: Eliminación del recurso temporal
            try {
                if (tempFile != null) Files.deleteIfExists(tempFile);
            } catch (Exception ignored) {}
        }
    }
}