package com.example.bapptest.data.repository

import com.example.bapptest.data.remote.RacksApi
import com.example.bapptest.model.RackRemoto
import com.example.bapptest.model.RackRemotoSolicitud
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RackRemotoRepositoryTest {

    private val api = mockk<RacksApi>()
    private val repositorio = RackRemotoRepository(api)
    private val rack = RackRemoto(id = 1, numero = 3, modelo = "Dell 237", encargado = "Allan", capacidad = 250)
    private val solicitud = RackRemotoSolicitud(numero = 3, modelo = "Dell 237", encargado = "Allan", capacidad = 250)

    @Test
    fun obtenerRacks_devuelveLaListaDelServicio() = runTest {
        coEvery { api.obtenerRacks() } returns listOf(rack)

        assertEquals(listOf(rack), repositorio.obtenerRacks())
    }

    @Test
    fun obtenerRack_consultaElIdIndicado() = runTest {
        coEvery { api.obtenerRack(1) } returns rack

        assertEquals(rack, repositorio.obtenerRack(1))
    }

    @Test
    fun crearRack_enviaLaSolicitudAlServicio() = runTest {
        coEvery { api.crearRack(solicitud) } returns rack

        assertEquals(rack, repositorio.crearRack(solicitud))
        coVerify(exactly = 1) { api.crearRack(solicitud) }
    }

    @Test
    fun actualizarRack_enviaIdYSolicitud() = runTest {
        coEvery { api.actualizarRack(1, solicitud) } returns rack

        assertEquals(rack, repositorio.actualizarRack(1, solicitud))
    }

    @Test
    fun eliminarRack_llamaAlEndpointDeBorrado() = runTest {
        coEvery { api.eliminarRack(1) } just Runs

        repositorio.eliminarRack(1)

        coVerify(exactly = 1) { api.eliminarRack(1) }
    }
}
