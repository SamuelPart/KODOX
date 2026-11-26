"""
Genera la locución en off (español) para el anuncio KODOX (60s).
Uso:
  pip install edge-tts
  python scripts/generate_voiceover.py
Genera: public/audio/voiceover.mp3
"""
import asyncio
import os
import edge_tts

VOICE = "es-MX-JorgeNeural"  # Masculina, mexicana, firme y natural
OUTPUT = os.path.join(os.path.dirname(__file__), "..", "public", "audio", "voiceover.mp3")

TEXT = """
Cada día... se pierden oportunidades en silencio.

Un mensaje sin responder. Una pregunta que llega tarde. Un cliente que se cansa de esperar.

No es falta de ganas. Es falta de claridad.

Cuando la información está dispersa... horarios por un lado, precios por otro, detalles en mil lugares... todo se vuelve más difícil.

Pero cuando todo está claro... las respuestas llegan rápido. La confusión desaparece. Y el tiempo vuelve a lo importante.

Organizar la información no es un lujo. Es una forma de cuidar el tiempo de las personas.

La tecnología debería simplificar la vida... no complicarla.

KODOX. Claridad que se nota.
""".strip()

async def main():
    os.makedirs(os.path.dirname(OUTPUT), exist_ok=True)
    communicate = edge_tts.Communicate(TEXT, VOICE, rate="-8%", pitch="-1Hz")
    await communicate.save(OUTPUT)
    print(f"Voz generada: {os.path.abspath(OUTPUT)}")

if __name__ == "__main__":
    asyncio.run(main())
