package com.walipet.engine.model

/**
 * Modo de comparación entre el mensaje entrante y el disparador de una regla.
 */
enum class MatchMode {
    /** El mensaje debe ser idéntico al disparador (ignorando espacios). */
    EXACT,

    /** El mensaje debe contener el disparador como subcadena. */
    CONTAINS,

    /** El mensaje debe empezar con el disparador. */
    STARTS_WITH,

    /** El disparador se interpreta como expresión regular. */
    REGEX,
}

/**
 * Ámbito de chats donde aplica una regla.
 */
enum class ChatScope {
    /** Responde en chats privados y grupos. */
    ALL,

    /** Solo chats privados (1 a 1). */
    PRIVATE_ONLY,

    /** Solo grupos. */
    GROUPS_ONLY,
}

/**
 * Tipo de chat detectado en la notificación de WhatsApp.
 */
enum class ChatType {
    PRIVATE,
    GROUP,
}
