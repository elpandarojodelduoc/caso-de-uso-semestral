package com.example.bapptest.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bapptest.R
import com.example.bapptest.ui.components.PantallaBapp
import com.example.bapptest.ui.theme.CampoClaro
import com.example.bapptest.ui.theme.Turquesa

private const val DURACION_CARGA_MS = 3000

@Composable
fun CargaScreen(alTerminar: () -> Unit) {
    BackHandler(enabled = true) {}

    val progreso = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progreso.animateTo(1f, tween(durationMillis = DURACION_CARGA_MS, easing = LinearEasing))
        alTerminar()
    }

    val transicion = rememberInfiniteTransition(label = "transicionCarga")
    val giro by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing)),
        label = "giroAnillo"
    )
    val pulso by transicion.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulsoLogo"
    )

    PantallaBapp(desplazable = false, arregloVertical = Arrangement.Center) {
        Box(modifier = Modifier.size(210.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val grosor = 10.dp.toPx()
                val esquina = Offset(grosor / 2, grosor / 2)
                val tamano = Size(size.width - grosor, size.height - grosor)
                drawArc(
                    color = CampoClaro.copy(alpha = 0.3f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = esquina,
                    size = tamano,
                    style = Stroke(width = grosor)
                )
                rotate(giro) {
                    drawArc(
                        color = Turquesa,
                        startAngle = 0f,
                        sweepAngle = 110f,
                        useCenter = false,
                        topLeft = esquina,
                        size = tamano,
                        style = Stroke(width = grosor, cap = StrokeCap.Round)
                    )
                }
            }
            Image(
                painter = painterResource(R.drawable.logo_cmc),
                contentDescription = "Logo CMC Ciberseguridad OT",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(120.dp)
                    .scale(pulso)
            )
        }
        Spacer(Modifier.height(28.dp))
        Text(
            text = "Iniciando sesión...",
            color = Color.White,
            fontSize = 22.sp
        )
        Spacer(Modifier.height(18.dp))
        LinearProgressIndicator(
            progress = { progreso.value },
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Turquesa,
            trackColor = CampoClaro.copy(alpha = 0.4f)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "${(progreso.value * 100).toInt()}%",
            color = Color.White,
            fontSize = 16.sp
        )
    }
}
