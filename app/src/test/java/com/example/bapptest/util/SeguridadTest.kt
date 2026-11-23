package com.example.bapptest.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class SeguridadTest {

    @Test
    fun hashClave_coincideConSha256Conocido() {
        assertEquals(
            "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
            Seguridad.hashClave("abc")
        )
    }

    @Test
    fun hashClave_mismaClaveGeneraMismoHash() {
        assertEquals(Seguridad.hashClave("Clave1234"), Seguridad.hashClave("Clave1234"))
    }

    @Test
    fun hashClave_clavesDistintasGeneranHashDistinto() {
        assertNotEquals(Seguridad.hashClave("Clave1234"), Seguridad.hashClave("clave1234"))
    }
}
