package com.example.bapptest.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bapptest.ui.components.BotonBapp
import com.example.bapptest.ui.components.DialogoConfirmacion
import com.example.bapptest.ui.components.EncabezadoBapp
import com.example.bapptest.ui.components.PantallaBapp
import com.example.bapptest.ui.components.TituloPantalla
import com.example.bapptest.ui.theme.AmarilloAlerta
import com.example.bapptest.ui.theme.CampoClaro
import com.example.bapptest.ui.theme.CianBoton
import com.example.bapptest.ui.theme.ColorError
import com.example.bapptest.ui.theme.FilaOscura
import com.example.bapptest.ui.theme.TarjetaAzul
import com.example.bapptest.ui.theme.VerdeValidado
import com.example.bapptest.viewmodel.ComponenteBorrador
import com.example.bapptest.viewmodel.LevantamientoViewModel

@Composable
fun RevisionScreen(
    viewModel: LevantamientoViewModel,
    correoUsuario: String,
    alGuardar: () -> Unit,
    alVolverACatalogo: () -> Unit
) {
    val contexto = LocalContext.current
    val componentes = viewModel.borrador.componentes
    val totalValidados = componentes.count { it.validado }
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    PantallaBapp {
        EncabezadoBapp()
        TituloPantalla("Revisión y validación")
        if (componentes.isEmpty()) {
            Text(
                text = "No hay componentes en el registro.\nVuelve al catálogo para agregarlos.",
                color = Color.White,
                fontSize = 17.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 24.dp)
            )
        } else {
            Text(
                text = "$totalValidados de ${componentes.size} componentes validados",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            componentes.forEach { componente ->
                TarjetaRevision(
                    componente = componente,
                    alCambiarValidacion = { viewModel.alternarValidacion(componente.nombre) }
                )
                Spacer(Modifier.height(10.dp))
            }
        }
        viewModel.errorGuardado?.let {
            Text(text = it, color = ColorError, fontSize = 14.sp, textAlign = TextAlign.Center)
        }
        Spacer(Modifier.height(10.dp))
        BotonBapp(
            texto = "Guardar y finalizar\nlevantamiento",
            alPulsar = { mostrarConfirmacion = true },
            habilitado = componentes.isNotEmpty() && !viewModel.guardando,
            colorFondo = CianBoton,
            borde = BorderStroke(4.dp, AmarilloAlerta),
            icono = { Icon(Icons.Default.Check, contentDescription = "Guardar") }
        )
        Spacer(Modifier.height(12.dp))
        BotonBapp(
            texto = "Regresar a catálogo\nde componentes",
            alPulsar = alVolverACatalogo,
            colorFondo = CampoClaro,
            subrayado = true,
            icono = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar") }
        )
    }

    if (mostrarConfirmacion) {
        DialogoConfirmacion(
            titulo = "Guardar levantamiento",
            mensaje = "Se guardará ${viewModel.borrador.idRack} con $totalValidados de ${componentes.size} componentes validados.",
            textoConfirmar = "Guardar",
            alConfirmar = {
                mostrarConfirmacion = false
                viewModel.guardar(correoUsuario) {
                    Toast.makeText(contexto, "Levantamiento guardado", Toast.LENGTH_SHORT).show()
                    alGuardar()
                }
            },
            alCerrar = { mostrarConfirmacion = false }
        )
    }
}

@Composable
private fun TarjetaRevision(
    componente: ComponenteBorrador,
    alCambiarValidacion: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(10.dp))
            .background(FilaOscura)
    ) {
        Box(
            modifier = Modifier
                .width(8.dp)
                .fillMaxHeight()
                .background(CianBoton)
        )
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = componente.nombre,
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(4.dp))
                EtiquetaDato(componente.categoria)
                Spacer(Modifier.height(6.dp))
                Text(text = "Descripción", color = Color.White, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                EtiquetaDato(componente.descripcion)
            }
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (componente.validado) Icons.Default.Check else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (componente.validado) VerdeValidado else AmarilloAlerta,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = if (componente.validado) "Validado" else "Pendiente",
                        color = if (componente.validado) VerdeValidado else AmarilloAlerta,
                        fontSize = 15.sp
                    )
                }
                Spacer(Modifier.height(8.dp))
                Switch(
                    checked = componente.validado,
                    onCheckedChange = { alCambiarValidacion() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = CianBoton,
                        checkedBorderColor = Color.Transparent,
                        uncheckedThumbColor = Color.Black,
                        uncheckedTrackColor = CampoClaro,
                        uncheckedBorderColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Composable
private fun EtiquetaDato(texto: String) {
    Text(
        text = texto,
        color = Color.White.copy(alpha = 0.7f),
        fontSize = 14.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(TarjetaAzul)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    )
}
