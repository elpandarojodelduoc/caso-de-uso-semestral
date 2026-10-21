package com.example.bapptest.util

import java.security.MessageDigest

object Seguridad {

    fun hashClave(clave: String): String {
        val resumen = MessageDigest.getInstance("SHA-256").digest(clave.toByteArray())
        return resumen.joinToString("") { "%02x".format(it) }
    }
}
