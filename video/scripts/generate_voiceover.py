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

VOICE = "es-MX-JorgeNeural"
OUTPUT = os.path.join(os.path.dirname(__file__), "..", "public", "audio", "voiceover.mp3")

TEXT = """
¿Tu negocio pierde clientes... sin que te des cuenta?

Mensajes sin responder. Clientes preguntando lo mismo. Pedidos que se confunden. Y tú, corriendo de un lado a otro.

No es falta de esfuerzo. Es que te falta un sistema claro.

En KODOX resolvemos ese problema.

Creamos páginas y cartas digitales rápidas, donde tus clientes ven tu menú, hacen su pedido y te escriben por WhatsApp en segundos.

Sin confusión. Sin mensajes perdidos. Sin perder ventas.

Tu negocio se ve profesional, tus clientes encuentran lo que buscan, y tú recuperas el control.

Más claridad. Más pedidos. Más tiempo para ti.

KODOX. Haz que tu negocio trabaje contigo, no contra ti.
""".strip()

async def main():
    os.makedirs(os.path.dirname(OUTPUT), exist_ok=True)
    communicate = edge_tts.Communicate(TEXT, VOICE, rate="-2%", pitch="-1Hz")
    await communicate.save(OUTPUT)
    print(f"Voz generada: {os.path.abspath(OUTPUT)}")

if __name__ == "__main__":
    asyncio.run(main())
