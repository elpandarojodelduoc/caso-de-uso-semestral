package com.example.bapptest.viewmodel

import com.example.bapptest.data.repository.ClimaRepository
import com.example.bapptest.model.CiudadEncontrada
import com.example.bapptest.model.ClimaDelSitio
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ClimaViewModelTest {

    private val repositorio = mockk<ClimaRepository>()
    private lateinit var viewModel: ClimaViewModel
    private val santiago = CiudadEncontrada("Santiago", -33.45, -70.64, "Chile")
    private val climaSantiago = ClimaDelSitio("Santiago, Chile", 21.5, 40.0, 12.0)

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = ClimaViewModel(repositorio)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun buscarPorCiudad_vacia_pideIngresarUnaCiudad() = runTest {
        viewModel.cambiarCiudad("   ")

        viewModel.buscarPorCiudad()

        assertEquals("Ingresa el nombre de una ciudad", viewModel.error)
        coVerify(exactly = 0) { repositorio.buscarCiudad(any()) }
    }

    @Test
    fun buscarPorCiudad_encontrada_muestraElClimaDelLugar() = runTest {
        coEvery { repositorio.buscarCiudad("Santiago") } returns santiago
        coEvery { repositorio.obtenerClima("Santiago, Chile", -33.45, -70.64) } returns climaSantiago
        viewModel.cambiarCiudad(" Santiago ")

        viewModel.buscarPorCiudad()

        assertEquals(climaSantiago, viewModel.clima)
        assertNull(viewModel.error)
        assertFalse(viewModel.consultando)
    }

    @Test
    fun buscarPorCiudad_noEncontrada_informaQueNoExiste() = runTest {
        coEvery { repositorio.buscarCiudad("Xyz") } returns null
        viewModel.cambiarCiudad("Xyz")

        viewModel.buscarPorCiudad()

        assertNull(viewModel.clima)
        assertEquals("No se encontró la ciudad \"Xyz\"", viewModel.error)
        assertFalse(viewModel.consultando)
    }

    @Test
    fun buscarPorCiudad_conFalloDeRed_informaElProblema() = runTest {
        coEvery { repositorio.buscarCiudad("Santiago") } throws RuntimeException("sin red")
        viewModel.cambiarCiudad("Santiago")

        viewModel.buscarPorCiudad()

        assertNull(viewModel.clima)
        assertEquals("No se pudo consultar el clima. Revisa tu conexión.", viewModel.error)
        assertFalse(viewModel.consultando)
    }

    @Test
    fun buscarPorUbicacion_usaElNombreDeLugarDelGps() = runTest {
        val climaGps = ClimaDelSitio(ClimaViewModel.LUGAR_GPS, 15.0, 55.0, 8.0)
        coEvery { repositorio.obtenerClima(ClimaViewModel.LUGAR_GPS, -33.0, -70.0) } returns climaGps

        viewModel.buscarPorUbicacion(-33.0, -70.0)

        assertEquals(climaGps, viewModel.clima)
        assertNull(viewModel.error)
    }

    @Test
    fun informarUbicacionNoDisponible_guardaElMotivo() {
        viewModel.informarUbicacionNoDisponible("GPS apagado")

        assertEquals("GPS apagado", viewModel.error)
    }

    @Test
    fun cambiarCiudad_limpiaElErrorAnterior() {
        viewModel.informarUbicacionNoDisponible("GPS apagado")

        viewModel.cambiarCiudad("Valparaíso")

        assertEquals("Valparaíso", viewModel.ciudad)
        assertNull(viewModel.error)
    }
}
