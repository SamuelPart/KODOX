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
        ServiceLocator.getInstance(this)
    }
}
