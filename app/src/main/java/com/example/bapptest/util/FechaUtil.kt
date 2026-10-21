package com.example.bapptest.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object FechaUtil {

    private const val FORMATO = "dd/MM/yyyy"
    private val zonaUtc = TimeZone.getTimeZone("UTC")

    fun hoy(): String = SimpleDateFormat(FORMATO, Locale.getDefault()).format(Date())

    fun hoyEnMillis(): Long = aMillis(hoy()) ?: System.currentTimeMillis()

    fun aMillis(fecha: String): Long? {
        return try {
            val formato = SimpleDateFormat(FORMATO, Locale.getDefault()).apply {
                timeZone = zonaUtc
                isLenient = false
            }
            formato.parse(fecha)?.time
        } catch (e: Exception) {
            null
        }
    }

    fun desdeMillis(milisegundos: Long): String {
        val formato = SimpleDateFormat(FORMATO, Locale.getDefault()).apply { timeZone = zonaUtc }
        return formato.format(Date(milisegundos))
    }
}
