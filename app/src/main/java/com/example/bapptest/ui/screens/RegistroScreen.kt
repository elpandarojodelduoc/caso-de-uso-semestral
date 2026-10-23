package com.example.bapptest.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.bapptest.ui.components.BotonBapp
import com.example.bapptest.ui.components.CampoBapp
import com.example.bapptest.ui.components.DialogoInformativo
import com.example.bapptest.ui.components.EncabezadoBapp
import com.example.bapptest.ui.components.PantallaBapp
import com.example.bapptest.ui.components.TituloPantalla
import com.example.bapptest.ui.theme.CampoClaro
import com.example.bapptest.viewmodel.AuthViewModel

@Composable
fun RegistroScreen(
    viewModel: AuthViewModel,
    alVolver: () -> Unit
) {
    val formulario = viewModel.formularioRegistro
    var mostrarCuentaCreada by remember { mutableStateOf(false) }

    PantallaBapp {
        EncabezadoBapp()
        TituloPantalla("Crear cuenta")
        Spacer(Modifier.height(12.dp))
        CampoBapp(
            valor = formulario.correo,
            alCambiar = viewModel::cambiarCorreoRegistro,
            etiqueta = "Correo",
            error = formulario.errorCorreo,
            tipoTeclado = KeyboardType.Email
        )
        Spacer(Modifier.height(22.dp))
        CampoBapp(
            valor = formulario.clave,
            alCambiar = viewModel::cambiarClaveRegistro,
            etiqueta = "Crear contraseña (8 caracteres mínimo)",
            error = formulario.errorClave,
            esClave = true,
            claveVisible = formulario.claveVisible,
            alAlternarClave = viewModel::alternarVisibilidadRegistro
        )
        Spacer(Modifier.height(30.dp))
        BotonBapp(
            texto = "Crear cuenta",
            alPulsar = { viewModel.crearCuenta { mostrarCuentaCreada = true } },
            habilitado = !formulario.procesando,
            icono = { Icon(Icons.Default.Check, contentDescription = "Crear cuenta") },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(16.dp))
        BotonBapp(
            texto = "Regresar el menú\nprincipal",
            alPulsar = {
                viewModel.limpiarRegistro()
                alVolver()
            },
            colorFondo = CampoClaro,
            icono = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar") },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }

    if (mostrarCuentaCreada) {
        DialogoInformativo(
            titulo = "Cuenta creada",
            mensaje = "Tu cuenta se registró correctamente. Ya puedes iniciar sesión.",
            textoBoton = "Ir al inicio de sesión",
            alCerrar = {
                mostrarCuentaCreada = false
                alVolver()
            }
        )
    }
}
