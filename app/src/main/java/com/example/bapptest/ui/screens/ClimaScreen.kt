package com.example.bapptest.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bapptest.model.ClimaDelSitio
import com.example.bapptest.ui.components.BotonBapp
import com.example.bapptest.ui.components.CampoBapp
import com.example.bapptest.ui.components.EncabezadoBapp
import com.example.bapptest.ui.components.PantallaBapp
import com.example.bapptest.ui.components.TituloPantalla
import com.example.bapptest.ui.theme.CampoClaro
import com.example.bapptest.ui.theme.CianBorde
import com.example.bapptest.ui.theme.ColorError
import com.example.bapptest.ui.theme.FilaOscura
import com.example.bapptest.util.UbicacionUtil
import com.example.bapptest.viewmodel.ClimaViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun ClimaScreen(
    viewModel: ClimaViewModel,
    alVolver: () -> Unit
) {
    val contexto = LocalContext.current
    val alcance = rememberCoroutineScope()

    fun consultarClimaConGps() {
        alcance.launch {
            val ubicacion = UbicacionUtil.obtenerUbicacionActual(contexto)
            if (ubicacion == null) {
                viewModel.informarUbicacionNoDisponible(
                    "No se pudo obtener tu ubicación. Activa el GPS e inténtalo nuevamente."
                )
            } else {
                viewModel.buscarPorUbicacion(ubicacion.latitude, ubicacion.longitude)
            }
        }
    }

    val solicitarPermisos = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultados ->
        if (resultados.values.any { concedido -> concedido }) {
            consultarClimaConGps()
        } else {
            viewModel.informarUbicacionNoDisponible("Necesitas conceder el permiso de ubicación para usar el GPS.")
        }
    }

    PantallaBapp {
        EncabezadoBapp()
        TituloPantalla("Clima del sitio")
        Text(
            text = "Consulta la temperatura exterior del lugar donde está el data center.",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        CampoBapp(
            valor = viewModel.ciudad,
            alCambiar = viewModel::cambiarCiudad,
            etiqueta = "Ciudad"
        )
        Spacer(Modifier.height(14.dp))
        BotonBapp(
            texto = "Buscar clima",
            alPulsar = viewModel::buscarPorCiudad,
            habilitado = !viewModel.consultando,
            icono = { Icon(Icons.Default.Search, contentDescription = null) }
        )
        Spacer(Modifier.height(10.dp))
        BotonBapp(
            texto = "Usar mi ubicación (GPS)",
            alPulsar = {
                if (UbicacionUtil.tienePermiso(contexto)) {
                    consultarClimaConGps()
                } else {
                    solicitarPermisos.launch(UbicacionUtil.permisosUbicacion)
                }
            },
            habilitado = !viewModel.consultando,
            colorFondo = CampoClaro,
            icono = { Icon(Icons.Default.LocationOn, contentDescription = null) }
        )
        Spacer(Modifier.height(18.dp))
        when {
            viewModel.consultando -> CircularProgressIndicator(color = CianBorde)

            viewModel.error != null -> Text(
                text = viewModel.error.orEmpty(),
                color = ColorError,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            viewModel.clima != null -> TarjetaClima(viewModel.clima!!)
        }
        Spacer(Modifier.height(18.dp))
        BotonBapp(
            texto = "Regresar el menú\nprincipal",
            alPulsar = alVolver,
            colorFondo = CampoClaro,
            icono = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar") }
        )
    }
}

@Composable
private fun TarjetaClima(clima: ClimaDelSitio) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(FilaOscura)
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = clima.lugar, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Text(
            text = String.format(Locale.US, "%.1f °C", clima.temperatura),
            color = CianBorde,
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = String.format(Locale.US, "Humedad: %.0f %%", clima.humedad),
            color = Color.White,
            fontSize = 16.sp
        )
        Text(
            text = String.format(Locale.US, "Viento: %.1f km/h", clima.vientoKmH),
            color = Color.White,
            fontSize = 16.sp
        )
    }
}
