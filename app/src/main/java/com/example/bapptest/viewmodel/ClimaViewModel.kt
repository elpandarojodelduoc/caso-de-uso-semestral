package com.example.bapptest.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bapptest.data.repository.ClimaRepository
import com.example.bapptest.model.ClimaDelSitio
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class ClimaViewModel(
    private val repositorio: ClimaRepository = ClimaRepository()
) : ViewModel() {

    var ciudad by mutableStateOf("")
        private set

    var clima by mutableStateOf<ClimaDelSitio?>(null)
        private set

    var consultando by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun cambiarCiudad(valor: String) {
        ciudad = valor
        error = null
    }

    fun buscarPorCiudad() {
        val nombre = ciudad.trim()
        if (nombre.isEmpty()) {
            error = "Ingrese el nombre de una ciudad"
            return
        }
        consultar("No se encontró la ciudad \"$nombre\"") {
            repositorio.buscarCiudad(nombre)?.let { encontrada ->
                val lugar = listOfNotNull(encontrada.nombre, encontrada.pais).joinToString(", ")
                repositorio.obtenerClima(lugar, encontrada.latitud, encontrada.longitud)
            }
        }
    }

    fun buscarPorUbicacion(latitud: Double, longitud: Double) {
        consultar("No se pudo obtener el clima de su ubicación") {
            repositorio.obtenerClima(LUGAR_GPS, latitud, longitud)
        }
    }

    fun informarUbicacionNoDisponible(motivo: String) {
        error = motivo
    }

    private fun consultar(mensajeSinResultado: String, consulta: suspend () -> ClimaDelSitio?) {
        if (consultando) return
        consultando = true
        error = null
        viewModelScope.launch {
            try {
                val resultado = consulta()
                clima = resultado
                if (resultado == null) error = mensajeSinResultado
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                clima = null
                error = "No se pudo consultar el clima. Revise su conexión."
            } finally {
                consultando = false
            }
        }
    }

    companion object {
        const val LUGAR_GPS = "Mi ubicación actual (GPS)"
    }
}
