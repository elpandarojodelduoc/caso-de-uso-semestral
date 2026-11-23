package com.example.bapptest.viewmodel

import com.example.bapptest.data.repository.RackRemotoRepository
import com.example.bapptest.model.RackRemoto
import com.example.bapptest.model.RackRemotoSolicitud
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RacksRemotosViewModelTest {

    private val repositorio = mockk<RackRemotoRepository>()
    private lateinit var viewModel: RacksRemotosViewModel
    private val rack = RackRemoto(id = 1, numero = 3, modelo = "Dell 237", encargado = "Allan", capacidad = 250)

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = RacksRemotosViewModel(repositorio)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    private fun completarFormulario() {
        viewModel.cambiarNumero("3")
        viewModel.cambiarModelo("Dell 237")
        viewModel.cambiarEncargado("Allan")
        viewModel.cambiarCapacidad("250")
    }

    @Test
    fun cargarRacks_guardaLaListaYTerminaDeCargar() = runTest {
        coEvery { repositorio.obtenerRacks() } returns listOf(rack)

        viewModel.cargarRacks()

        assertEquals(listOf(rack), viewModel.racks)
        assertFalse(viewModel.cargando)
        assertNull(viewModel.mensaje)
    }

    @Test
    fun cargarRacks_conError_informaAlUsuario() = runTest {
        coEvery { repositorio.obtenerRacks() } throws RuntimeException("sin red")

        viewModel.cargarRacks()

        assertEquals("No se pudieron cargar los racks. Revisa tu conexión.", viewModel.mensaje)
        assertFalse(viewModel.cargando)
    }

    @Test
    fun abrirFormularioNuevo_abreFormularioVacio() {
        viewModel.abrirFormularioNuevo()

        assertEquals(FormularioRackRemoto(), viewModel.formulario)
    }

    @Test
    fun abrirFormularioEdicion_cargaLosDatosActualesDelServidor() = runTest {
        coEvery { repositorio.obtenerRack(1) } returns rack

        viewModel.abrirFormularioEdicion(1)

        val formulario = viewModel.formulario
        assertNotNull(formulario)
        assertEquals(1, formulario!!.idEdicion)
        assertEquals("3", formulario.numero)
        assertEquals("Dell 237", formulario.modelo)
        assertEquals("Allan", formulario.encargado)
        assertEquals("250", formulario.capacidad)
    }

    @Test
    fun abrirFormularioEdicion_conError_noAbreElFormulario() = runTest {
        coEvery { repositorio.obtenerRack(1) } throws RuntimeException("sin red")

        viewModel.abrirFormularioEdicion(1)

        assertNull(viewModel.formulario)
        assertEquals("No se pudo obtener el rack seleccionado.", viewModel.mensaje)
    }

    @Test
    fun cerrarFormulario_ocultaElFormulario() {
        viewModel.abrirFormularioNuevo()

        viewModel.cerrarFormulario()

        assertNull(viewModel.formulario)
    }

    @Test
    fun guardar_conCamposInvalidos_muestraErroresYNoLlamaAlServicio() = runTest {
        viewModel.abrirFormularioNuevo()

        viewModel.guardar()

        val formulario = viewModel.formulario!!
        assertNotNull(formulario.errorNumero)
        assertNotNull(formulario.errorModelo)
        assertNotNull(formulario.errorEncargado)
        assertNotNull(formulario.errorCapacidad)
        coVerify(exactly = 0) { repositorio.crearRack(any()) }
    }

    @Test
    fun cambiarCampo_limpiaElErrorDeEseCampo() {
        viewModel.abrirFormularioNuevo()
        viewModel.guardar()

        viewModel.cambiarModelo("HP")

        assertNull(viewModel.formulario!!.errorModelo)
        assertNotNull(viewModel.formulario!!.errorNumero)
    }

    @Test
    fun guardar_rackNuevo_creaCierraFormularioYRecargaLaLista() = runTest {
        val solicitud = RackRemotoSolicitud(numero = 3, modelo = "Dell 237", encargado = "Allan", capacidad = 250)
        coEvery { repositorio.crearRack(solicitud) } returns rack
        coEvery { repositorio.obtenerRacks() } returns listOf(rack)
        viewModel.abrirFormularioNuevo()
        completarFormulario()

        viewModel.guardar()

        coVerify(exactly = 1) { repositorio.crearRack(solicitud) }
        assertNull(viewModel.formulario)
        assertEquals("Rack creado correctamente.", viewModel.mensaje)
        assertEquals(listOf(rack), viewModel.racks)
    }

    @Test
    fun guardar_rackExistente_actualizaEnVezDeCrear() = runTest {
        val solicitud = RackRemotoSolicitud(numero = 3, modelo = "Dell 237", encargado = "Allan", capacidad = 250)
        coEvery { repositorio.obtenerRack(1) } returns rack
        coEvery { repositorio.actualizarRack(1, solicitud) } returns rack
        coEvery { repositorio.obtenerRacks() } returns listOf(rack)
        viewModel.abrirFormularioEdicion(1)

        viewModel.guardar()

        coVerify(exactly = 1) { repositorio.actualizarRack(1, solicitud) }
        coVerify(exactly = 0) { repositorio.crearRack(any()) }
        assertEquals("Rack actualizado correctamente.", viewModel.mensaje)
    }

    @Test
    fun guardar_conErrorDelServicio_mantieneElFormularioAbierto() = runTest {
        coEvery { repositorio.crearRack(any()) } throws RuntimeException("falló")
        viewModel.abrirFormularioNuevo()
        completarFormulario()

        viewModel.guardar()

        assertNotNull(viewModel.formulario)
        assertFalse(viewModel.formulario!!.guardando)
        assertEquals("No se pudo guardar el rack. Intenta nuevamente.", viewModel.mensaje)
    }

    @Test
    fun eliminar_borraElRackYRecargaLaLista() = runTest {
        coEvery { repositorio.eliminarRack(1) } just Runs
        coEvery { repositorio.obtenerRacks() } returns emptyList()

        viewModel.eliminar(rack)

        coVerify(exactly = 1) { repositorio.eliminarRack(1) }
        assertEquals("Rack eliminado correctamente.", viewModel.mensaje)
        assertEquals(emptyList<RackRemoto>(), viewModel.racks)
    }

    @Test
    fun eliminar_conError_informaYNoRecarga() = runTest {
        coEvery { repositorio.eliminarRack(1) } throws RuntimeException("falló")

        viewModel.eliminar(rack)

        assertEquals("No se pudo eliminar el rack.", viewModel.mensaje)
        coVerify(exactly = 0) { repositorio.obtenerRacks() }
    }

    @Test
    fun limpiarMensaje_quitaElMensajeActual() = runTest {
        coEvery { repositorio.obtenerRacks() } throws RuntimeException("sin red")
        viewModel.cargarRacks()

        viewModel.limpiarMensaje()

        assertNull(viewModel.mensaje)
    }
}
