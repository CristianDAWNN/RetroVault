import sys
import json
from google import genai

def recommend_games(api_key, user_request):
    try:
        client = genai.Client(api_key=api_key)

        prompt = f"""
        Actúa como un erudito de los videojuegos retro y modernos.
        El usuario ha hecho la siguiente petición: "{user_request}".
        
        Tu misión es recomendar los 5 mejores videojuegos que se ajusten EXACTAMENTE a lo que pide.
        - Si el usuario menciona una o varias CONSOLAS, DEBES limitar tus recomendaciones estrictamente a juegos disponibles en esas plataformas.
        - Si el usuario menciona un JUEGO SIMILAR, recomienda juegos con mecánicas o temáticas muy parecidas.
        - Si el usuario menciona un GÉNERO, céntrate en él.

        INSTRUCCIÓN CRÍTICA: Devuelve ÚNICAMENTE un JSON válido (una lista de objetos). No uses formato markdown (```json).
        Estructura exacta:
        [
            {{
                "title": "Nombre del juego",
                "console": "Consola (debe ser una de las pedidas por el usuario, si especificó alguna)",
                "reason": "Una breve frase épica de por qué debo jugarlo y por qué encaja con lo que ha pedido"
            }}
        ]
        """

        # Usamos flash porque es más rápido para texto
        response = client.models.generate_content(
            model="gemini-2.5-flash", 
            contents=prompt
        )

        # Limpiamos el texto por si Gemini mete markdown
        clean_json = response.text.replace("```json", "").replace("```", "").strip()
        print(clean_json)

    except Exception as e:
        print(json.dumps([{"error": str(e)}]))

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print(json.dumps([{"error": "Faltan argumentos"}]))
    else:
        # Argumentos: 1=API_KEY, 2=PETICION_DEL_USUARIO
        recommend_games(sys.argv[1], sys.argv[2])