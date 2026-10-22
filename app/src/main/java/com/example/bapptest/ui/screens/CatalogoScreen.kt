package com.example.bapptest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bapptest.model.CatalogoComponentes
import com.example.bapptest.model.ComponenteCatalogo
import com.example.bapptest.ui.components.BotonBapp
import com.example.bapptest.ui.components.EncabezadoBapp
import com.example.bapptest.ui.components.PantallaBapp
import com.example.bapptest.ui.components.TituloPantalla
import com.example.bapptest.ui.theme.AzulTarjeta
import com.example.bapptest.ui.theme.BordeMorado
import com.example.bapptest.ui.theme.CampoClaro
import com.example.bapptest.ui.theme.CianBorde
import com.example.bapptest.ui.theme.MoradoTarjeta
import com.example.bapptest.ui.theme.Turquesa
import com.example.bapptest.ui.theme.VerdeValidado
import com.example.bapptest.viewmodel.LevantamientoViewModel

@Composable
fun CatalogoScreen(
    viewModel: LevantamientoViewModel,
    alEditarRegistro: () -> Unit,
    alVolver: () -> Unit
) {
    val nombresSeleccionados = viewModel.borrador.componentes.map { it.nombre }.toSet()

    PantallaBapp {
        EncabezadoBapp()
        TituloPantalla("Catálogo de componentes")
        BotonBapp(
            texto = "Editar Registro",
            alPulsar = alEditarRegistro,
            colorFondo = CampoClaro,
            subrayado = true,
            icono = { Icon(Icons.Default.Edit, contentDescription = "Editar registro") },
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(Modifier.height(24.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Turquesa)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(width = 60.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Componentes destacados",
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            CatalogoComponentes.componentes.chunked(2).forEach { fila ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    fila.forEach { componente ->
                        TarjetaComponente(
                            componente = componente,
                            seleccionado = componente.nombre in nombresSeleccionados,
                            alPulsar = { viewModel.alternarComponenteCatalogo(componente) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (fila.size == 1) Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(14.dp))
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Toca un componente para agregarlo o quitarlo del registro.",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        BotonBapp(
            texto = "Regresar",
            alPulsar = alVolver,
            icono = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar") },
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun TarjetaComponente(
    componente: ComponenteCatalogo,
    seleccionado: Boolean,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorBorde = if (seleccionado) {
        Brush.verticalGradient(listOf(VerdeValidado, VerdeValidado))
    } else {
        Brush.verticalGradient(listOf(CianBorde, BordeMorado))
    }
    Box(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Brush.verticalGradient(listOf(MoradoTarjeta, AzulTarjeta)))
            .border(BorderStroke(3.dp, colorBorde), RoundedCornerShape(8.dp))
            .clickable(onClick = alPulsar),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = componente.nombre,
            color = Color.White,
            fontSize = 17.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        if (seleccionado) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Componente seleccionado",
                tint = VerdeValidado,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(22.dp)
            )
        }
    }
}
