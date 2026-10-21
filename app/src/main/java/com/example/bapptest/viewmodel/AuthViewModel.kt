package com.example.bapptest.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bapptest.data.local.AppDatabase
import com.example.bapptest.data.repository.BappRepository
import com.example.bapptest.util.Validadores
import kotlinx.coroutines.launch

data class FormularioAuth(
    val correo: String = "",
    val clave: String = "",
    val claveVisible: Boolean = false,
    val errorCorreo: String? = null,
    val errorClave: String? = null,
    val errorGeneral: String? = null,
    val procesando: Boolean = false
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = BappRepository(AppDatabase.obtener(application))

    var formularioLogin by mutableStateOf(FormularioAuth())
        private set

    var formularioRegistro by mutableStateOf(FormularioAuth())
        private set

    var correoSesion by mutableStateOf<String?>(null)
        private set

    fun cambiarCorreoLogin(valor: String) {
        formularioLogin = formularioLogin.copy(correo = valor, errorCorreo = null, errorGeneral = null)
    }

    fun cambiarClaveLogin(valor: String) {
        formularioLogin = formularioLogin.copy(clave = valor, errorClave = null, errorGeneral = null)
    }

    fun alternarVisibilidadLogin() {
        formularioLogin = formularioLogin.copy(claveVisible = !formularioLogin.claveVisible)
    }

    fun cambiarCorreoRegistro(valor: String) {
        formularioRegistro = formularioRegistro.copy(correo = valor, errorCorreo = null, errorGeneral = null)
    }

    fun cambiarClaveRegistro(valor: String) {
        formularioRegistro = formularioRegistro.copy(clave = valor, errorClave = null, errorGeneral = null)
    }

    fun alternarVisibilidadRegistro() {
        formularioRegistro = formularioRegistro.copy(claveVisible = !formularioRegistro.claveVisible)
    }

    fun iniciarSesion(alExito: () -> Unit) {
        val errorCorreo = Validadores.correo(formularioLogin.correo)
        val errorClave = Validadores.claveIngreso(formularioLogin.clave)
        if (errorCorreo != null || errorClave != null) {
            formularioLogin = formularioLogin.copy(errorCorreo = errorCorreo, errorClave = errorClave)
            return
        }
        if (formularioLogin.procesando) return
        formularioLogin = formularioLogin.copy(procesando = true)
        viewModelScope.launch {
            val correo = formularioLogin.correo.trim().lowercase()
            val credencialesValidas = repositorio.iniciarSesion(correo, formularioLogin.clave)
            if (credencialesValidas) {
                correoSesion = correo
                formularioLogin = formularioLogin.copy(procesando = false)
                alExito()
            } else {
                formularioLogin = formularioLogin.copy(
                    procesando = false,
                    errorGeneral = "Correo o contraseña incorrectos"
                )
            }
        }
    }

    fun crearCuenta(alExito: () -> Unit) {
        val errorCorreo = Validadores.correo(formularioRegistro.correo)
        val errorClave = Validadores.claveNueva(formularioRegistro.clave)
        if (errorCorreo != null || errorClave != null) {
            formularioRegistro = formularioRegistro.copy(errorCorreo = errorCorreo, errorClave = errorClave)
            return
        }
        if (formularioRegistro.procesando) return
        formularioRegistro = formularioRegistro.copy(procesando = true)
        viewModelScope.launch {
            val correo = formularioRegistro.correo.trim().lowercase()
            val cuentaCreada = repositorio.registrarUsuario(correo, formularioRegistro.clave)
            if (cuentaCreada) {
                formularioRegistro = FormularioAuth()
                formularioLogin = FormularioAuth(correo = correo)
                alExito()
            } else {
                formularioRegistro = formularioRegistro.copy(
                    procesando = false,
                    errorCorreo = "Ya existe una cuenta con este correo"
                )
            }
        }
    }

    fun limpiarRegistro() {
        formularioRegistro = FormularioAuth()
    }

    fun cerrarSesion() {
        correoSesion = null
        formularioLogin = FormularioAuth()
    }
}
