package com.example.bapptest.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bapptest.data.repository.RackRemotoRepository
import com.example.bapptest.model.RackRemoto
import com.example.bapptest.model.RackRemotoSolicitud
import com.example.bapptest.util.Validadores
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

data class FormularioRackRemoto(
    val idEdicion: Int? = null,
    val numero: String = "",
    val modelo: String = "",
    val encargado: String = "",
    val capacidad: String = "",
    val errorNumero: String? = null,
    val errorModelo: String? = null,
    val errorEncargado: String? = null,
    val errorCapacidad: String? = null,
    val guardando: Boolean = false
) {
    fun tieneErrores(): Boolean =
        errorNumero != null || errorModelo != null || errorEncargado != null || errorCapacidad != null
}

class RacksRemotosViewModel(
    private val repositorio: RackRemotoRepository = RackRemotoRepository()
) : ViewModel() {

    var racks by mutableStateOf<List<RackRemoto>>(emptyList())
        private set

    var cargando by mutableStateOf(false)
        private set

    var formulario by mutableStateOf<FormularioRackRemoto?>(null)
        private set

    var mensaje by mutableStateOf<String?>(null)
        private set

    fun cargarRacks() {
        viewModelScope.launch {
            cargando = true
            try {
                consultarRemoto("No se pudieron cargar los racks. Revise su conexión.") {
                    repositorio.obtenerRacks()
                }?.let { racks = it }
            } finally {
                cargando = false
            }
        }
    }

    fun abrirFormularioNuevo() {
        formulario = FormularioRackRemoto()
    }

    fun abrirFormularioEdicion(id: Int) {
        viewModelScope.launch {
            cargando = true
            try {
                consultarRemoto("No se pudo obtener el rack seleccionado.") {
                    repositorio.obtenerRack(id)
                }?.let { rack ->
                    formulario = FormularioRackRemoto(
                        idEdicion = rack.id,
                        numero = rack.numero.toString(),
                        modelo = rack.modelo,
                        encargado = rack.encargado,
                        capacidad = rack.capacidad.toString()
                    )
                }
            } finally {
                cargando = false
            }
        }
    }

    fun cerrarFormulario() {
        formulario = null
    }

    fun cambiarNumero(valor: String) {
        formulario = formulario?.copy(numero = valor, errorNumero = null)
    }

    fun cambiarModelo(valor: String) {
        formulario = formulario?.copy(modelo = valor, errorModelo = null)
    }

    fun cambiarEncargado(valor: String) {
        formulario = formulario?.copy(encargado = valor, errorEncargado = null)
    }

    fun cambiarCapacidad(valor: String) {
        formulario = formulario?.copy(capacidad = valor, errorCapacidad = null)
    }

    fun guardar() {
        val actual = formulario ?: return
        if (actual.guardando) return
        val validado = actual.copy(
            errorNumero = Validadores.numeroRack(actual.numero),
            errorModelo = Validadores.textoObligatorio(actual.modelo, "Ingrese el modelo del rack"),
            errorEncargado = Validadores.textoObligatorio(actual.encargado, "Ingrese el nombre del encargado"),
            errorCapacidad = Validadores.capacidadRack(actual.capacidad)
        )
        if (validado.tieneErrores()) {
            formulario = validado
            return
        }
        formulario = validado.copy(guardando = true)
        val solicitud = RackRemotoSolicitud(
            numero = validado.numero.trim().toInt(),
            modelo = validado.modelo.trim(),
            encargado = validado.encargado.trim(),
            capacidad = validado.capacidad.trim().toInt()
        )
        val idEdicion = validado.idEdicion
        viewModelScope.launch {
            val rackGuardado = consultarRemoto("No se pudo guardar el rack. Intente nuevamente.") {
                if (idEdicion == null) {
                    repositorio.crearRack(solicitud)
                } else {
                    repositorio.actualizarRack(idEdicion, solicitud)
                }
            }
            if (rackGuardado == null) {
                formulario = formulario?.copy(guardando = false)
                return@launch
            }
            formulario = null
            mensaje = if (idEdicion == null) "Rack creado correctamente." else "Rack actualizado correctamente."
            cargarRacks()
        }
    }

    fun eliminar(rack: RackRemoto) {
        viewModelScope.launch {
            val eliminado = consultarRemoto("No se pudo eliminar el rack.") {
                repositorio.eliminarRack(rack.id)
            }
            if (eliminado != null) {
                mensaje = "Rack eliminado correctamente."
                cargarRacks()
            }
        }
    }

    fun limpiarMensaje() {
        mensaje = null
    }

    private suspend fun <T> consultarRemoto(mensajeError: String, llamada: suspend () -> T): T? {
        return try {
            llamada()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            mensaje = mensajeError
            null
        }
    }
}
