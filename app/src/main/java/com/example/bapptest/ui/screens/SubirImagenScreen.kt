package com.example.bapptest.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bapptest.R
import com.example.bapptest.ui.components.BotonBapp
import com.example.bapptest.ui.components.EncabezadoBapp
import com.example.bapptest.ui.components.PantallaBapp
import com.example.bapptest.ui.components.TituloPantalla
import com.example.bapptest.ui.components.rememberImagen
import com.example.bapptest.ui.theme.CampoClaro
import com.example.bapptest.ui.theme.ColorError
import com.example.bapptest.ui.theme.TextoOscuroSuave
import com.example.bapptest.ui.theme.Turquesa
import com.example.bapptest.viewmodel.LevantamientoViewModel

@Composable
fun SubirImagenScreen(
    viewModel: LevantamientoViewModel,
    alSubir: () -> Unit,
    alVolver: () -> Unit
) {
    val contexto = LocalContext.current
    val uriImagen = viewModel.borrador.imagenUri
    val imagen by rememberImagen(uriImagen)

    val lanzadorGaleria = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) viewModel.establecerImagen(uri, contexto.contentResolver.getType(uri))
    }

    PantallaBapp {
        EncabezadoBapp()
        TituloPantalla("Subir Imagen")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Turquesa)
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_camara),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text("Cargar archivo", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = "solo archivos jpg, png o gif",
                color = TextoOscuroSuave,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 46.dp, top = 4.dp, bottom = 14.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        lanzadorGaleria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                    .drawBehind {
                        drawRoundRect(
                            color = Color.Black,
                            cornerRadius = CornerRadius(8.dp.toPx()),
                            style = Stroke(
                                width = 3.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 12f))
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                val mapaBits = imagen
                if (mapaBits != null) {
                    Image(
                        bitmap = mapaBits,
                        contentDescription = "Imagen seleccionada del rack",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "selecciona el archivo",
                            color = TextoOscuroSuave,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Icon(
                            painter = painterResource(R.drawable.ic_nube_subida),
                            contentDescription = "Subir archivo",
                            tint = Color.Black,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            }
        }
        AnimatedVisibility(visible = viewModel.errorImagen != null) {
            Text(
                text = viewModel.errorImagen.orEmpty(),
                color = ColorError,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BotonBapp(
                texto = "Eliminar",
                alPulsar = viewModel::eliminarImagen,
                habilitado = uriImagen != null,
                colorFondo = CampoClaro,
                icono = { Icon(Icons.Default.Delete, contentDescription = "Eliminar imagen") },
                modifier = Modifier.weight(1f)
            )
            BotonBapp(
                texto = "Subir",
                alPulsar = alSubir,
                habilitado = uriImagen != null,
                colorFondo = CampoClaro,
                subrayado = true,
                icono = { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Subir imagen") },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(14.dp))
        BotonBapp(
            texto = "Regresar",
            alPulsar = alVolver,
            icono = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar") }
        )
    }
}
