package com.example.bapptest.data.repository

import com.example.bapptest.data.remote.ClimaApi
import com.example.bapptest.data.remote.GeocodificacionApi
import com.example.bapptest.model.CiudadEncontrada
import com.example.bapptest.model.ClimaActual
import com.example.bapptest.model.RespuestaClima
import com.example.bapptest.model.RespuestaGeocodificacion
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ClimaRepositoryTest {

    private val climaApi = mockk<ClimaApi>()
    private val geocodificacionApi = mockk<GeocodificacionApi>()
    private val repositorio = ClimaRepository(climaApi, geocodificacionApi)
    private val santiago = CiudadEncontrada("Santiago", -33.45, -70.64, "Chile")

    @Test
    fun buscarCiudad_devuelveLaPrimeraCoincidencia() = runTest {
        coEvery { geocodificacionApi.buscarCiudad("Santiago") } returns RespuestaGeocodificacion(listOf(santiago))

        assertEquals(santiago, repositorio.buscarCiudad("Santiago"))
    }

    @Test
    fun buscarCiudad_sinResultados_devuelveNull() = runTest {
        coEvery { geocodificacionApi.buscarCiudad("Xyz") } returns RespuestaGeocodificacion(null)

        assertNull(repositorio.buscarCiudad("Xyz"))
    }

    @Test
    fun buscarCiudad_conListaVacia_devuelveNull() = runTest {
        coEvery { geocodificacionApi.buscarCiudad("Xyz") } returns RespuestaGeocodificacion(emptyList())

        assertNull(repositorio.buscarCiudad("Xyz"))
    }

    @Test
    fun obtenerClima_convierteLaRespuestaEnDatosDelSitio() = runTest {
        coEvery { climaApi.obtenerClimaActual(-33.45, -70.64) } returns
                RespuestaClima(ClimaActual(temperatura = 21.5, humedad = 40.0, vientoKmH = 12.0))

        val clima = repositorio.obtenerClima("Santiago, Chile", -33.45, -70.64)

        assertEquals("Santiago, Chile", clima.lugar)
        assertEquals(21.5, clima.temperatura, 0.001)
        assertEquals(40.0, clima.humedad, 0.001)
        assertEquals(12.0, clima.vientoKmH, 0.001)
    }
}
