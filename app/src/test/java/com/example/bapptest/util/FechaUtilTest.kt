package com.example.bapptest.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FechaUtilTest {

    @Test
    fun aMillis_fechaDeReferencia_devuelveCero() {
        assertEquals(0L, FechaUtil.aMillis("01/01/1970"))
    }

    @Test
    fun aMillis_textoInvalido_devuelveNull() {
        assertNull(FechaUtil.aMillis("no es una fecha"))
    }

    @Test
    fun aMillis_diaInexistente_devuelveNull() {
        assertNull(FechaUtil.aMillis("31/02/2026"))
    }

    @Test
    fun desdeMillis_convierteAlFormatoDiaMesAnio() {
        assertEquals("01/01/1970", FechaUtil.desdeMillis(0L))
    }

    @Test
    fun aMillis_y_desdeMillis_sonInversas() {
        val fecha = "23/11/2026"
        assertEquals(fecha, FechaUtil.desdeMillis(FechaUtil.aMillis(fecha)!!))
    }
}
