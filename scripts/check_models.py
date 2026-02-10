import google.generativeai as genai
import os
import sys

# Pega aquí tu API KEY directamente para probar rápido
api_key = "AIzaSyDdiF9u96wXVTLX8NnYG1MjB3GEMQmUpfY"

genai.configure(api_key=api_key)

print("--- MODELOS DISPONIBLES ---")
try:
    for m in genai.list_models():
        if 'generateContent' in m.supported_generation_methods:
            print(f"Nombre: {m.name}")
except Exception as e:
    print(f"Error al listar: {e}")