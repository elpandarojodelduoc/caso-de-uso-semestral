package com.example.bapptest.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bapptest.data.local.RackConComponentes
import com.example.bapptest.ui.components.BotonBapp
import com.example.bapptest.ui.components.DialogoConfirmacion
import com.example.bapptest.ui.components.EncabezadoBapp
import com.example.bapptest.ui.components.PantallaBapp
import com.example.bapptest.ui.components.TituloPantalla
import com.example.bapptest.ui.theme.AmarilloAlerta
import com.example.bapptest.ui.theme.CampoClaro
import com.example.bapptest.ui.theme.FilaOscura
import com.example.bapptest.ui.theme.SeparadorClaro
import com.example.bapptest.ui.theme.VerdeValidado
import com.example.bapptest.viewmodel.RacksViewModel

@Composable
fun RacksScreen(
    viewModel: RacksViewModel,
    correoUsuario: String,
    alAbrirRack: (Long) -> Unit,
    alVolver: () -> Unit
) {
    var rackPorEliminar by remember { mutableStateOf<RackConComponentes?>(null) }
    LaunchedEffect(correoUsuario) { viewModel.observar(correoUsuario) }

    PantallaBapp(desplazable = false) {
        EncabezadoBapp()
        TituloPantalla("Racks registrados")
        if (viewModel.racks.isEmpty()) {
            Spacer(Modifier.weight(1f))
            Text(
                text = "Aún no hay racks registrados.\nLevanta un rack nuevo para comenzar.",
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.weight(1f))
        } else {
            Text(
                text = "Toca un rack para revisarlo. Mantén presionado para eliminarlo.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SeparadorClaro),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(viewModel.racks, key = { it.rack.id }) { rack ->
                    FilaRack(
                        rack = rack,
                        alPulsar = { alAbrirRack(rack.rack.id) },
                        alMantener = { rackPorEliminar = rack }
                    )
                }
            }
            Spacer(Modifier.weight(1f))
        }
        BotonBapp(
            texto = "Regresar el menú\nprincipal",
            alPulsar = alVolver,
            colorFondo = CampoClaro,
            icono = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar") },
            modifier = Modifier.padding(top = 14.dp, start = 24.dp, end = 24.dp)
        )
    }

    rackPorEliminar?.let { rack ->
        DialogoConfirmacion(
            titulo = "Eliminar rack",
            mensaje = "¿Deseas eliminar ${rack.rack.idRack} y sus componentes registrados? Esta acción no se puede deshacer.",
            textoConfirmar = "Eliminar",
            alConfirmar = {
                viewModel.eliminar(rack)
                rackPorEliminar = null
            },
            alCerrar = { rackPorEliminar = null }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FilaRack(
    rack: RackConComponentes,
    alPulsar: () -> Unit,
    alMantener: () -> Unit
) {
    val revisionCompleta = rack.componentes.isNotEmpty() && rack.componentes.all { it.validado }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FilaOscura)
            .combinedClickable(onClick = alPulsar, onLongClick = alMantener)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = rack.rack.idRack,
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1.3f)
        )
        Text(
            text = "Rol:\n${rack.rack.rol}",
            color = Color.White,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Row(
            modifier = Modifier.weight(1.2f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = rack.rack.fecha, color = Color.White, fontSize = 13.sp)
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = if (revisionCompleta) Icons.Default.Check else Icons.Default.Warning,
                contentDescription = if (revisionCompleta) "Revisión completa" else "Revisión pendiente",
                tint = if (revisionCompleta) VerdeValidado else AmarilloAlerta,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
