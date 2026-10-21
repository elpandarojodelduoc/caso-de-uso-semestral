package com.example.bapptest.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bapptest.ui.components.EncabezadoBapp
import com.example.bapptest.ui.components.PantallaBapp
import com.example.bapptest.ui.components.TituloPantalla
import com.example.bapptest.ui.theme.Turquesa

@Composable
fun MenuScreen(
    correoUsuario: String,
    alLevantarRack: () -> Unit,
    alVerRacks: () -> Unit,
    alCerrarSesion: () -> Unit
) {
    var opcionesVisibles by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { opcionesVisibles = true }

    PantallaBapp(desplazable = false) {
        EncabezadoBapp()
        Spacer(Modifier.height(24.dp))
        TituloPantalla("Selecciona una opción")
        Text(
            text = correoUsuario,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp
        )
        Spacer(Modifier.height(40.dp))
        AnimatedVisibility(
            visible = opcionesVisibles,
            enter = fadeIn(tween(700)) + slideInVertically(tween(700)) { it / 3 }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                BotonOpcionMenu(
                    texto = "Levantar un\nRack nuevo",
                    icono = Icons.Default.Add,
                    alPulsar = alLevantarRack,
                    modifier = Modifier.weight(1f)
                )
                BotonOpcionMenu(
                    texto = "Racks\nregistrados",
                    icono = Icons.AutoMirrored.Filled.List,
                    alPulsar = alVerRacks,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(Modifier.weight(1f))
        TextButton(onClick = alCerrarSesion) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión", tint = Color.White)
            Spacer(Modifier.size(8.dp))
            Text("Cerrar sesión", color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
private fun BotonOpcionMenu(
    texto: String,
    icono: ImageVector,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = alPulsar,
        modifier = modifier.height(170.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Turquesa, contentColor = Color.White)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icono, contentDescription = null, modifier = Modifier.size(40.dp))
            Spacer(Modifier.height(12.dp))
            Text(
                text = texto,
                fontSize = 19.sp,
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}
