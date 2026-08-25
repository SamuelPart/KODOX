# Reglas ProGuard/R8 de WaliPet (release).
# Room genera código compatible con R8; estas reglas son defensivas.

# Entidades y DAOs de Room: nunca se deben ofuscar sus nombres de columnas.
-keep class com.walipet.data.local.entity.** { *; }
-keep class com.walipet.data.local.AppDatabase { *; }

# Keep los campos reflejados de los modelos del motor (usados por repositorios).
-keep class com.walipet.engine.model.** { *; }

# NotificationListenerService y RemoteInput requieren nombres de clase estables
# porque se referencian desde el AndroidManifest y el sistema.
-keep class com.walipet.service.** { *; }
