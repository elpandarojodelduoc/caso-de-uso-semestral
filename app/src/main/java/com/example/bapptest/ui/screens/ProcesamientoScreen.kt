package com.example.bapptest.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bapptest.R
import com.example.bapptest.ui.components.BotonBapp
import com.example.bapptest.ui.components.EncabezadoBapp
import com.example.bapptest.ui.components.PantallaBapp
import com.example.bapptest.ui.components.TituloPantalla
import com.example.bapptest.ui.components.rememberImagen
import com.example.bapptest.ui.theme.CampoClaro
import com.example.bapptest.ui.theme.CianBorde
import com.example.bapptest.ui.theme.ColorError
import com.example.bapptest.ui.theme.Turquesa
import com.example.bapptest.viewmodel.EstadoProcesamiento
import com.example.bapptest.viewmodel.LevantamientoViewModel

@Composable
fun ProcesamientoScreen(
    viewModel: LevantamientoViewModel,
    alIrACatalogo: () -> Unit,
    alVolverAlMenu: () -> Unit
) {
    LaunchedEffect(Unit) { viewModel.iniciarProcesamiento() }

    val estado = viewModel.estadoProcesamiento
    val imagen by rememberImagen(viewModel.borrador.imagenUri)

    val transicion = rememberInfiniteTransition(label = "transicionProcesamiento")
    val avanceBarrido by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing), RepeatMode.Reverse),
        label = "barridoEscaneo"
    )
    val faseDePuntos by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing)),
        label = "puntosSuspensivos"
    )
    val puntos = ".".repeat(faseDePuntos.toInt().coerceIn(0, 3) + 1)
    val procesando = estado == EstadoProcesamiento.Procesando

    PantallaBapp {
        EncabezadoBapp()
        TituloPantalla("Procesamiento de imagen")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Turquesa.copy(alpha = 0.35f))
                .drawWithContent {
                    drawContent()
                    if (procesando) {
                        val altoLinea = 6.dp.toPx()
                        drawRect(
                            color = CianBorde.copy(alpha = 0.85f),
                            topLeft = Offset(0f, (size.height - altoLinea) * avanceBarrido),
                            size = Size(size.width, altoLinea)
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            val mapaBits = imagen
            if (mapaBits != null) {
                Image(
                    bitmap = mapaBits,
                    contentDescription = "Imagen del rack en procesamiento",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_imagen),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Turquesa)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_imagen),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(44.dp)
            )
            Spacer(Modifier.width(10.dp))
            Crossfade(
                targetState = estado,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF14707D)),
                label = "mensajeEstado"
            ) { estadoActual ->
                Text(
                    text = when (estadoActual) {
                        EstadoProcesamiento.Listo -> "¡Imagen procesada!"
                        EstadoProcesamiento.Error -> "No se pudo procesar"
                        else -> "Su imagen está siendo procesada..."
                    },
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        if (procesando) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = CianBorde,
                trackColor = CampoClaro.copy(alpha = 0.4f)
            )
            Spacer(Modifier.height(10.dp))
        }
        Text(
            text = when (estado) {
                EstadoProcesamiento.Listo -> "Se identificaron ${viewModel.borrador.componentes.size} componentes"
                EstadoProcesamiento.Error -> "Revisa la imagen e intenta nuevamente"
                else -> "Identificando equipos de red$puntos"
            },
            color = if (estado == EstadoProcesamiento.Error) ColorError else Color.White,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(18.dp))
        if (estado == EstadoProcesamiento.Error) {
            BotonBapp(
                texto = "Reintentar",
                alPulsar = viewModel::reintentarProcesamiento
            )
        } else {
            BotonBapp(
                texto = "Ir a catálogo de\ncomponentes",
                alPulsar = alIrACatalogo,
                habilitado = estado == EstadoProcesamiento.Listo,
                subrayado = true
            )
        }
        Spacer(Modifier.height(12.dp))
        BotonBapp(
            texto = "Regresar al menú\nprincipal click aquí",
            alPulsar = alVolverAlMenu,
            colorFondo = CampoClaro,
            subrayado = true,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}
