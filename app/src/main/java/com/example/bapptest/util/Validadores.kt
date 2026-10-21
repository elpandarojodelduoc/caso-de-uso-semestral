package com.example.bapptest.util

import android.util.Patterns

object Validadores {

    private const val LARGO_MINIMO_CLAVE = 8
    private val patronIdRack = Regex("^[A-Za-z0-9-]{3,20}$")

    fun correo(valor: String): String? = when {
        valor.isBlank() -> "Ingresa tu correo"
        !Patterns.EMAIL_ADDRESS.matcher(valor.trim()).matches() -> "El correo no tiene un formato válido"
        else -> null
    }

    fun claveNueva(valor: String): String? = when {
        valor.isBlank() -> "Ingresa una contraseña"
        valor.length < LARGO_MINIMO_CLAVE -> "La contraseña debe tener al menos $LARGO_MINIMO_CLAVE caracteres"
        else -> null
    }

    fun claveIngreso(valor: String): String? = if (valor.isBlank()) "Ingresa tu contraseña" else null

    fun idRack(valor: String): String? = when {
        valor.isBlank() -> "El ID del rack es obligatorio"
        !patronIdRack.matches(valor.trim()) -> "Usa de 3 a 20 caracteres: letras, números o guiones"
        else -> null
    }

    fun fecha(valor: String): String? {
        val milisegundos = FechaUtil.aMillis(valor) ?: return "Selecciona una fecha válida"
        return if (milisegundos > FechaUtil.hoyEnMillis()) "La fecha no puede ser futura" else null
    }
}
