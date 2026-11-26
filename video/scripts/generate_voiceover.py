"""
Genera la locución en off (español) para el anuncio KODOX.
Uso:
  pip install edge-tts
  python scripts/generate_voiceover.py
Genera: public/audio/voiceover.mp3
"""
import asyncio
import os
import edge_tts

VOICE = "es-PE-CamilaNeural"  # Voz peruana; alternativa: es-MX-DaliaNeural
OUTPUT = os.path.join(os.path.dirname(__file__), "..", "public", "audio", "voiceover.mp3")

TEXT = """
¿Y si el problema no es tu trabajo... sino todo lo que pasa alrededor?

Mensajes que se pierden. Preguntas repetidas. Tiempo que se va.

Un cliente escribe... y la respuesta llega tarde. No por descuido, sino por falta de claridad.

La información está dispersa: horarios, precios, detalles... todo en lugares distintos.

Pero cuando las personas encuentran respuestas rápido, todo fluye mejor. Menos fricción. Menos estrés.

Organizar la información no es un lujo. Es una forma de cuidar el tiempo de los demás.

La tecnología debería simplificar la vida... no complicarla.

Claridad. Orden. Tranquilidad.
""".strip()

async def main():
    os.makedirs(os.path.dirname(OUTPUT), exist_ok=True)
    communicate = edge_tts.Communicate(TEXT, VOICE, rate="-5%", pitch="+0Hz")
    await communicate.save(OUTPUT)
    print(f"Voz generada: {os.path.abspath(OUTPUT)}")

if __name__ == "__main__":
    asyncio.run(main())
