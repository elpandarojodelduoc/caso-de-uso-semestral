package com.example.bapptest.util

import android.util.Patterns

object Validadores {

    private const val LARGO_MINIMO_CLAVE = 8
    private val patronIdRack = Regex("^[A-Za-z0-9-]{3,20}$")

    fun correo(valor: String): String? = when {
        valor.isBlank() -> "Ingrese su correo"
        !Patterns.EMAIL_ADDRESS.matcher(valor.trim()).matches() -> "El correo no tiene un formato valido"
        else -> null
    }

    fun claveNueva(valor: String): String? = when {
        valor.isBlank() -> "Ingrese una contraseña"
        valor.length < LARGO_MINIMO_CLAVE -> "La contraseña debe tener al menos $LARGO_MINIMO_CLAVE caracteres"
        else -> null
    }

    fun claveIngreso(valor: String): String? = if (valor.isBlank()) "Ingrese su contraseña" else null

    fun idRack(valor: String): String? = when {
        valor.isBlank() -> "El ID del rack es obligatorio"
        !patronIdRack.matches(valor.trim()) -> "Usa de 3 a 20 caracteres: letras, números o guiones"
        else -> null
    }

    fun numeroRack(valor: String): String? = when {
        valor.isBlank() -> "Ingrese el número del rack"
        (valor.trim().toIntOrNull() ?: 0) <= 0 -> "El número debe ser un entero mayor que 0"
        else -> null
    }

    fun capacidadRack(valor: String): String? = when {
        valor.isBlank() -> "Ingrese la capacidad del rack"
        (valor.trim().toIntOrNull() ?: 0) <= 0 -> "La capacidad debe ser un entero mayor que 0"
        else -> null
    }

    fun textoObligatorio(valor: String, mensajeError: String): String? =
        if (valor.isBlank()) mensajeError else null

    fun fecha(valor: String): String? {
        val milisegundos = FechaUtil.aMillis(valor) ?: return "Seleccione una fecha válida"
        return if (milisegundos > FechaUtil.hoyEnMillis()) "La fecha no puede ser futura" else null
    }
}
