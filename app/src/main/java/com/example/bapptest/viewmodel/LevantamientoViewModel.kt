package com.example.bapptest.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bapptest.data.local.AppDatabase
import com.example.bapptest.data.local.ComponenteEntity
import com.example.bapptest.data.local.RackEntity
import com.example.bapptest.data.repository.BappRepository
import com.example.bapptest.model.CatalogoComponentes
import com.example.bapptest.model.ComponenteCatalogo
import com.example.bapptest.util.FechaUtil
import com.example.bapptest.util.ImagenUtil
import com.example.bapptest.util.Validadores
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private const val TIEMPO_PROCESAMIENTO_MS = 3000L
private val TIPOS_IMAGEN_PERMITIDOS = setOf("image/jpeg", "image/jpg", "image/png", "image/gif")

val ROLES_DISPONIBLES = listOf("Inspector", "Administrador")

enum class EstadoProcesamiento { Inactivo, Procesando, Listo, Error }

data class ComponenteBorrador(
    val nombre: String,
    val categoria: String,
    val descripcion: String,
    val validado: Boolean = false
)

data class BorradorLevantamiento(
    val idRack: String = "",
    val fecha: String = FechaUtil.hoy(),
    val rol: String = ROLES_DISPONIBLES.first(),
    val imagenUri: Uri? = null,
    val rutaImagenGuardada: String = "",
    val componentes: List<ComponenteBorrador> = emptyList(),
    val rackEditandoId: Long? = null
)

class LevantamientoViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = BappRepository(AppDatabase.obtener(application))

    var borrador by mutableStateOf(BorradorLevantamiento())
        private set

    var errorIdRack by mutableStateOf<String?>(null)
        private set

    var errorFecha by mutableStateOf<String?>(null)
        private set

    var errorImagen by mutableStateOf<String?>(null)
        private set

    var errorGuardado by mutableStateOf<String?>(null)
        private set

    var estadoProcesamiento by mutableStateOf(EstadoProcesamiento.Inactivo)
        private set

    var guardando by mutableStateOf(false)
        private set

    fun cambiarIdRack(valor: String) {
        borrador = borrador.copy(idRack = valor.uppercase())
        errorIdRack = null
    }

    fun cambiarFecha(valor: String) {
        borrador = borrador.copy(fecha = valor)
        errorFecha = null
    }

    fun cambiarRol(valor: String) {
        borrador = borrador.copy(rol = valor)
    }

    fun validarFormulario(): Boolean {
        errorIdRack = Validadores.idRack(borrador.idRack)
        errorFecha = Validadores.fecha(borrador.fecha)
        return errorIdRack == null && errorFecha == null
    }

    fun establecerImagen(uri: Uri, tipoMime: String?) {
        if (tipoMime == null || tipoMime !in TIPOS_IMAGEN_PERMITIDOS) {
            errorImagen = "Solo se permiten archivos jpg, png o gif"
            return
        }
        errorImagen = null
        borrador = borrador.copy(imagenUri = uri, rutaImagenGuardada = "", componentes = emptyList())
        estadoProcesamiento = EstadoProcesamiento.Inactivo
    }

    fun eliminarImagen() {
        errorImagen = null
        borrador = borrador.copy(imagenUri = null, rutaImagenGuardada = "", componentes = emptyList())
        estadoProcesamiento = EstadoProcesamiento.Inactivo
    }

    fun iniciarProcesamiento() {
        if (estadoProcesamiento != EstadoProcesamiento.Inactivo) return
        val uriOrigen = borrador.imagenUri ?: return
        estadoProcesamiento = EstadoProcesamiento.Procesando
        viewModelScope.launch {
            val rutaGuardada = withContext(Dispatchers.IO) {
                ImagenUtil.copiarAAlmacenamientoInterno(getApplication<Application>(), uriOrigen)
            }
            delay(TIEMPO_PROCESAMIENTO_MS)
            if (rutaGuardada == null) {
                estadoProcesamiento = EstadoProcesamiento.Error
                return@launch
            }
            val detectados = CatalogoComponentes.detectar(borrador.idRack).map { it.aBorrador() }
            borrador = borrador.copy(rutaImagenGuardada = rutaGuardada, componentes = detectados)
            estadoProcesamiento = EstadoProcesamiento.Listo
        }
    }

    fun reintentarProcesamiento() {
        estadoProcesamiento = EstadoProcesamiento.Inactivo
        iniciarProcesamiento()
    }

    fun alternarComponenteCatalogo(componente: ComponenteCatalogo) {
        val existente = borrador.componentes.any { it.nombre == componente.nombre }
        val actualizados = if (existente) {
            borrador.componentes.filterNot { it.nombre == componente.nombre }
        } else {
            borrador.componentes + componente.aBorrador()
        }
        borrador = borrador.copy(componentes = actualizados)
    }

    fun alternarValidacion(nombreComponente: String) {
        borrador = borrador.copy(
            componentes = borrador.componentes.map {
                if (it.nombre == nombreComponente) it.copy(validado = !it.validado) else it
            }
        )
    }

    fun cargarRack(rackId: Long, alCargar: () -> Unit) {
        viewModelScope.launch {
            val rackGuardado = repositorio.obtenerRack(rackId) ?: return@launch
            borrador = BorradorLevantamiento(
                idRack = rackGuardado.rack.idRack,
                fecha = rackGuardado.rack.fecha,
                rol = rackGuardado.rack.rol,
                imagenUri = Uri.fromFile(File(rackGuardado.rack.rutaImagen)),
                rutaImagenGuardada = rackGuardado.rack.rutaImagen,
                componentes = rackGuardado.componentes.map {
                    ComponenteBorrador(it.nombre, it.categoria, it.descripcion, it.validado)
                },
                rackEditandoId = rackGuardado.rack.id
            )
            errorGuardado = null
            estadoProcesamiento = EstadoProcesamiento.Listo
            alCargar()
        }
    }

    fun guardar(correoUsuario: String, alTerminar: () -> Unit) {
        if (guardando) return
        guardando = true
        errorGuardado = null
        viewModelScope.launch {
            try {
                val rack = RackEntity(
                    idRack = borrador.idRack.trim(),
                    fecha = borrador.fecha,
                    rol = borrador.rol,
                    rutaImagen = borrador.rutaImagenGuardada,
                    correoUsuario = correoUsuario
                )
                val componentes = borrador.componentes.map {
                    ComponenteEntity(
                        rackId = 0,
                        nombre = it.nombre,
                        categoria = it.categoria,
                        descripcion = it.descripcion,
                        validado = it.validado
                    )
                }
                repositorio.guardarRack(borrador.rackEditandoId, rack, componentes)
                alTerminar()
            } catch (e: Exception) {
                errorGuardado = "No se pudo guardar el levantamiento. Intenta nuevamente"
            } finally {
                guardando = false
            }
        }
    }

    fun reiniciar() {
        borrador = BorradorLevantamiento()
        errorIdRack = null
        errorFecha = null
        errorImagen = null
        errorGuardado = null
        estadoProcesamiento = EstadoProcesamiento.Inactivo
    }

    private fun ComponenteCatalogo.aBorrador() = ComponenteBorrador(nombre, categoria, descripcion)
}
