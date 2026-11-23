package com.example.bapptest.model

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test


class ModelosJsonTest {

    private val gson = Gson()

    @Test
    fun rackRemoto_seLeeDesdeRespuestaDeXano_ignorandoCamposExtra() {
        val json = """{"id":1,"created_at":1795002612035,"numero":3,"modelo":"Dell 237","encargado":"Allan","capacidad":250}"""

        val rack = gson.fromJson(json, RackRemoto::class.java)

        assertEquals(RackRemoto(id = 1, numero = 3, modelo = "Dell 237", encargado = "Allan", capacidad = 250), rack)
    }

    @Test
    fun respuestaClima_seLeeConLosNombresDeOpenMeteo() {
        val json = """{"latitude":-33.45,"current":{"time":"2026-11-23T20:00","temperature_2m":18.4,"relative_humidity_2m":52,"wind_speed_10m":9.7}}"""

        val actual = gson.fromJson(json, RespuestaClima::class.java).actual

        assertEquals(18.4, actual.temperatura, 0.001)
        assertEquals(52.0, actual.humedad, 0.001)
        assertEquals(9.7, actual.vientoKmH, 0.001)
    }

    @Test
    fun respuestaGeocodificacion_leeLaCiudadEncontrada() {
        val json = """{"results":[{"id":3871336,"name":"Santiago","latitude":-33.45694,"longitude":-70.64827,"country":"Chile"}]}"""

        val ciudad = gson.fromJson(json, RespuestaGeocodificacion::class.java).resultados!!.first()

        assertEquals("Santiago", ciudad.nombre)
        assertEquals("Chile", ciudad.pais)
        assertEquals(-33.45694, ciudad.latitud, 0.00001)
    }

    @Test
    fun respuestaGeocodificacion_sinCoincidencias_traeResultadosNulos() {
        val respuesta = gson.fromJson("""{"generationtime_ms":0.5}""", RespuestaGeocodificacion::class.java)

        assertNull(respuesta.resultados)
    }
}
