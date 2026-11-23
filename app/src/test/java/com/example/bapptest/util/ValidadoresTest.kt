package com.example.bapptest.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidadoresTest {

    @Test
    fun claveNueva_vacia_devuelveError() {
        assertEquals("Ingresa una contraseña", Validadores.claveNueva("  "))
    }

    @Test
    fun claveNueva_muyCorta_devuelveError() {
        assertNotNull(Validadores.claveNueva("abc1234"))
    }

    @Test
    fun claveNueva_conOchoCaracteres_esValida() {
        assertNull(Validadores.claveNueva("abcd1234"))
    }

    @Test
    fun claveIngreso_vacia_devuelveError_y_conTexto_esValida() {
        assertEquals("Ingresa tu contraseña", Validadores.claveIngreso(""))
        assertNull(Validadores.claveIngreso("x"))
    }

    @Test
    fun idRack_validaLargoYCaracteres() {
        assertEquals("El ID del rack es obligatorio", Validadores.idRack(""))
        assertNotNull(Validadores.idRack("ab"))
        assertNotNull(Validadores.idRack("rack_01"))
        assertNull(Validadores.idRack("RACK-01"))
    }

    @Test
    fun numeroRack_rechazaVacioCeroYTexto() {
        assertEquals("Ingresa el número del rack", Validadores.numeroRack(""))
        assertNotNull(Validadores.numeroRack("0"))
        assertNotNull(Validadores.numeroRack("-4"))
        assertNotNull(Validadores.numeroRack("abc"))
        assertNull(Validadores.numeroRack(" 12 "))
    }

    @Test
    fun capacidadRack_rechazaVacioCeroYTexto() {
        assertEquals("Ingresa la capacidad del rack", Validadores.capacidadRack(" "))
        assertNotNull(Validadores.capacidadRack("0"))
        assertNotNull(Validadores.capacidadRack("2.5"))
        assertNull(Validadores.capacidadRack("250"))
    }

    @Test
    fun textoObligatorio_devuelveMensajeSoloSiEstaVacio() {
        assertEquals("falta", Validadores.textoObligatorio("   ", "falta"))
        assertNull(Validadores.textoObligatorio("Dell", "falta"))
    }

    @Test
    fun fecha_deHoy_esValida() {
        assertNull(Validadores.fecha(FechaUtil.hoy()))
    }

    @Test
    fun fecha_futura_devuelveError() {
        assertEquals("La fecha no puede ser futura", Validadores.fecha("01/01/2999"))
    }

    @Test
    fun fecha_inexistente_devuelveError() {
        assertEquals("Selecciona una fecha válida", Validadores.fecha("31/02/2026"))
    }
}
