import sys
import json
import os
from google import genai
from google.genai import types

def analyze_image(api_key, image_path):
    try:
        # 1. Conectar con el cliente nuevo de Google
        client = genai.Client(api_key=api_key)

        # 2. Leer la imagen en bytes
        with open(image_path, "rb") as f:
            image_bytes = f.read()

    # 3. Prompt (VERSIÓN MAESTRA: EXTRACTOR DE DATOS RETRO)
        prompt = """
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
            "rate": 8 (Nota entera estimada de 0 a 10)
        }
        """

        # 4. Generar contenido usando el modelo que SI tienes (Gemini 2.0 Flash)
        response = client.models.generate_content(
            model="gemini-flash-latest",
                contents=[
                types.Content(
                    parts=[
                        types.Part.from_text(text=prompt),
                        types.Part.from_bytes(data=image_bytes, mime_type="image/jpeg")
                    ]
                )
            ]
        )

        # 5. Imprimir respuesta limpia para Java
        print(response.text)

    except Exception as e:
        # En caso de error, devolvemos un JSON de error
        print(json.dumps({"error": str(e)}))

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print(json.dumps({"error": "Faltan argumentos"}))
    else:
        # Argumentos que vienen de Java: 1=API_KEY, 2=RUTA_IMAGEN
        analyze_image(sys.argv[1], sys.argv[2])