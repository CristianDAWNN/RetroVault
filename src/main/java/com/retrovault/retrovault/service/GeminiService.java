package com.retrovault.retrovault.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    // ESCÁNER DE IMÁGENES CON IA
    public String analyzeImage(MultipartFile file) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            String prompt = """
                Analiza esta portada de videojuego como un experto coleccionista y archivero.
                Tu misión es extraer los metadatos exactos del juego.
                INSTRUCCIONES CRÍTICAS DE PLATAFORMA (CONSOLE):
                1. Identifica el LOGOTIPO de la consola en la carátula (franja superior/lateral).
                2. Sé ESPECÍFICO con las variantes. 
                   - MAL: "Game Boy" (cuando es Advance). BIEN: "GBA" o "Game Boy Advance".
                   - MAL: "Xbox" (cuando es 360). BIEN: "Xbox 360".
                3. Usa preferiblemente estas SIGLAS ESTÁNDAR si estás seguro:
                   - Sony: PS1, PS2, PS3, PS4, PS5, PSP, Vita
                   - Nintendo: NES, SNES, N64, GC, Wii, WiiU, Switch, GB, GBC, GBA, DS, 3DS
                   - Sega: Master System, Genesis, Saturn, Dreamcast, Game Gear
                   - Microsoft: Xbox, Xbox 360, Xbox One, Series X
                   - Retro: Atari 2600, Neo Geo, PC Engine, C64, Amiga
                INSTRUCCIONES DE CONOCIMIENTO (CEREBRO vs OJOS):
                - Si la carátula es confusa o no tiene logo, USA TU CONOCIMIENTO.
                - Ejemplo: Si ves "God of War II", sabes que es PS2. Si ves "Halo 3", sabes que es Xbox 360.
                - Prioriza la plataforma original de lanzamiento.
                FORMATO DE RESPUESTA (JSON PURO):
                {
                    "title": "Título limpio y coloquial (ej: Pokémon Amarillo)",
                    "console": "Sigla o nombre corto (según lista arriba)",
                    "genre": "Género principal en español (ej: RPG, Plataformas, Acción...)",
                    "release_date": "YYYY-MM-DD (Fecha exacta de lanzamiento original)",
                    "rate": 8
                }
                """;

            String jsonPayload = """
                {
                  "contents": [{
                    "parts": [
                      {"text": "%s"},
                      {"inline_data": { "mime_type": "image/jpeg", "data": "%s" }}
                    ]
                  }]
                }
                """.formatted(prompt.replace("\"", "\\\"").replace("\n", " "), base64Image);

            return callGeminiApi(jsonPayload, "gemini-2.5-flash");

        } catch (Exception e) {
            return "{\"error\": \"Excepción en el servicio de análisis nativo: " + e.getMessage() + "\"}";
        }
    }

    // RECOMENDADOR DE JUEGOS CON IA
    public String recommendGames(String userRequest) {
        try {
            String prompt = """
                Actúa como un erudito de los videojuegos retro y modernos.
                El usuario ha hecho la siguiente petición: "%s".
                
                Tu misión es recomendar los 5 mejores videojuegos que se ajusten EXACTAMENTE a lo que pide.
                - Si el usuario menciona una o varias CONSOLAS, DEBES limitar tus recomendaciones estrictamente a juegos disponibles en esas plataformas.
                - Si el usuario menciona un JUEGO SIMILAR, recomienda juegos con mecánicas o temáticas muy parecidas.
                - Si el usuario menciona un GÉNERO, céntrate en él.

                INSTRUCCIÓN CRÍTICA: Devuelve ÚNICAMENTE un JSON válido (una lista de objetos). No uses formato markdown.
                Estructura exacta:
                [
                    {
                        "title": "Nombre del juego",
                        "console": "Consola (debe ser una de las pedidas por el usuario, si especificó alguna)",
                        "reason": "Una breve frase épica de por qué debo jugarlo y por qué encaja con lo que ha pedido"
                    }
                ]
                """.formatted(userRequest.replace("\"", "\\\"").replace("\n", " "));

            String safePrompt = prompt.replace("\"", "\\\"").replace("\n", " ");

            String jsonPayload = """
                {
                  "contents": [{
                    "parts": [
                      {"text": "%s"}
                    ]
                  }]
                }
                """.formatted(safePrompt);

            return callGeminiApi(jsonPayload, "gemini-2.5-flash");

        } catch (Exception e) {
            return "[{\"error\": \"Excepción en el recomendador nativo: " + e.getMessage() + "\"}]";
        }
    }

    // MÉTODO AUXILIAR QUE LLAMA A GOOGLE
    private String callGeminiApi(String jsonPayload, String model) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Parseamos la respuesta de Google para sacar el texto que nos interesa
        JsonNode rootNode = objectMapper.readTree(response.body());
        if (rootNode.has("error")) {
             throw new RuntimeException("Error de API: " + rootNode.path("error").path("message").asText());
        }
        
        String extractedText = rootNode.path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();

        // Limpiamos los bloques de markdown (```json y ```)
        return extractedText.replaceAll("```json", "").replaceAll("```", "").trim();
    }
}