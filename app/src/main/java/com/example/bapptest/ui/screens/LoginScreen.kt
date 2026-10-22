package com.example.bapptest.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import com.example.bapptest.ui.components.BotonBapp
import com.example.bapptest.ui.components.CampoBapp
import com.example.bapptest.ui.components.EncabezadoBapp
import com.example.bapptest.ui.components.PantallaBapp
import com.example.bapptest.ui.components.TituloPantalla
import com.example.bapptest.ui.theme.ColorError
import com.example.bapptest.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    alIrARegistro: () -> Unit,
    alIniciarSesion: () -> Unit
) {
    val formulario = viewModel.formularioLogin
    var encabezadoVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { encabezadoVisible = true }

    PantallaBapp {
        AnimatedVisibility(
            visible = encabezadoVisible,
            enter = fadeIn(tween(700)) + scaleIn(tween(700), initialScale = 0.8f)
        ) {
            EncabezadoBapp()
        }
        TituloPantalla("Inicio de sesión")
        Spacer(Modifier.height(12.dp))
        CampoBapp(
            valor = formulario.correo,
            alCambiar = viewModel::cambiarCorreoLogin,
            etiqueta = "Correo",
            error = formulario.errorCorreo,
            tipoTeclado = KeyboardType.Email
        )
        Spacer(Modifier.height(14.dp))
        CampoBapp(
            valor = formulario.clave,
            alCambiar = viewModel::cambiarClaveLogin,
            etiqueta = "Contraseña",
            error = formulario.errorClave,
            esClave = true,
            claveVisible = formulario.claveVisible,
            alAlternarClave = viewModel::alternarVisibilidadLogin
        )
        AnimatedVisibility(visible = formulario.errorGeneral != null) {
            Text(
                text = formulario.errorGeneral.orEmpty(),
                color = ColorError,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
        }
        Spacer(Modifier.height(30.dp))
        BotonBapp(
            texto = "Ingresar",
            alPulsar = { viewModel.iniciarSesion(alIniciarSesion) },
            habilitado = !formulario.procesando,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(22.dp))
        BotonBapp(
            texto = "¿No tienes una cuenta?\nCrea una nueva",
            alPulsar = alIrARegistro,
            subrayado = true,
            icono = { Icon(Icons.Default.Person, contentDescription = "Crear cuenta", tint = Color.Black) },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
