package com.walipet

import android.app.Application
import com.walipet.di.ServiceLocator
import com.walipet.service.NotificationChannels

/** Application de WaliPet: crea canales y arranca el contenedor de dependencias. */
class WaliPetApp : Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationChannels.create(this)
        // Inicializa el ServiceLocator (y siembra mascota + reglas de ejemplo).
        // Precalienta la voz para que el primer mensaje se lea en ≤ 2 s.
        ServiceLocator.getInstance(this).preloadVoiceOver()
    }
}
