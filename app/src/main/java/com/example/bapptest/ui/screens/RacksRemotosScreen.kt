package com.example.bapptest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bapptest.model.RackRemoto
import com.example.bapptest.ui.components.BotonBapp
import com.example.bapptest.ui.components.CampoBapp
import com.example.bapptest.ui.components.DialogoConfirmacion
import com.example.bapptest.ui.components.DialogoInformativo
import com.example.bapptest.ui.components.EncabezadoBapp
import com.example.bapptest.ui.components.PantallaBapp
import com.example.bapptest.ui.components.TituloPantalla
import com.example.bapptest.ui.theme.CampoClaro
import com.example.bapptest.ui.theme.CianBorde
import com.example.bapptest.ui.theme.FilaOscura
import com.example.bapptest.ui.theme.SeparadorClaro
import com.example.bapptest.ui.theme.TarjetaAzul
import com.example.bapptest.viewmodel.FormularioRackRemoto
import com.example.bapptest.viewmodel.RacksRemotosViewModel

@Composable
fun RacksRemotosScreen(
    viewModel: RacksRemotosViewModel,
    alVolver: () -> Unit
) {
    var rackPorEliminar by remember { mutableStateOf<RackRemoto?>(null) }
    LaunchedEffect(Unit) { viewModel.cargarRacks() }

    PantallaBapp(desplazable = false) {
        EncabezadoBapp()
        TituloPantalla("Racks en la nube")
        when {
            viewModel.cargando && viewModel.racks.isEmpty() -> {
                Spacer(Modifier.weight(1f))
                CircularProgressIndicator(color = CianBorde)
                Spacer(Modifier.weight(1f))
            }

            viewModel.racks.isEmpty() -> {
                Spacer(Modifier.weight(1f))
                Text(
                    text = "No hay racks en el servidor.\nAgrega el primero.",
                    color = Color.White,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.weight(1f))
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SeparadorClaro),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(viewModel.racks, key = { it.id }) { rack ->
                        FilaRackRemoto(
                            rack = rack,
                            alEditar = { viewModel.abrirFormularioEdicion(rack.id) },
                            alEliminar = { rackPorEliminar = rack }
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
            }
        }
        BotonBapp(
            texto = "Agregar rack",
            alPulsar = viewModel::abrirFormularioNuevo,
            icono = { Icon(Icons.Default.Add, contentDescription = null) },
            modifier = Modifier.padding(top = 14.dp, start = 24.dp, end = 24.dp)
        )
        BotonBapp(
            texto = "Regresar el menú\nprincipal",
            alPulsar = alVolver,
            colorFondo = CampoClaro,
            icono = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar") },
            modifier = Modifier.padding(top = 10.dp, start = 24.dp, end = 24.dp)
        )
    }

    viewModel.formulario?.let { formulario ->
        DialogoFormularioRack(formulario = formulario, viewModel = viewModel)
    }

    rackPorEliminar?.let { rack ->
        DialogoConfirmacion(
            titulo = "Eliminar rack",
            mensaje = "¿Deseas eliminar el rack N° ${rack.numero} (${rack.modelo}) del servidor? Esta acción no se puede deshacer.",
            textoConfirmar = "Eliminar",
            alConfirmar = {
                viewModel.eliminar(rack)
                rackPorEliminar = null
            },
            alCerrar = { rackPorEliminar = null }
        )
    }

    viewModel.mensaje?.let { mensaje ->
        DialogoInformativo(
            titulo = "Aviso",
            mensaje = mensaje,
            textoBoton = "Aceptar",
            alCerrar = viewModel::limpiarMensaje
        )
    }
}

@Composable
private fun FilaRackRemoto(
    rack: RackRemoto,
    alEditar: () -> Unit,
    alEliminar: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FilaOscura)
            .padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Rack N° ${rack.numero} · ${rack.modelo}",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Encargado: ${rack.encargado} · Capacidad: ${rack.capacidad}",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp
            )
        }
        IconButton(onClick = alEditar) {
            Icon(Icons.Default.Edit, contentDescription = "Editar rack ${rack.numero}", tint = CianBorde)
        }
        IconButton(onClick = alEliminar) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar rack ${rack.numero}", tint = Color.White)
        }
    }
}

@Composable
private fun DialogoFormularioRack(
    formulario: FormularioRackRemoto,
    viewModel: RacksRemotosViewModel
) {
    val esEdicion = formulario.idEdicion != null
    AlertDialog(
        onDismissRequest = viewModel::cerrarFormulario,
        containerColor = TarjetaAzul,
        titleContentColor = Color.White,
        title = { Text(if (esEdicion) "Editar rack" else "Nuevo rack", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CampoBapp(
                    valor = formulario.numero,
                    alCambiar = viewModel::cambiarNumero,
                    etiqueta = "Número",
                    error = formulario.errorNumero,
                    tipoTeclado = KeyboardType.Number
                )
                CampoBapp(
                    valor = formulario.modelo,
                    alCambiar = viewModel::cambiarModelo,
                    etiqueta = "Modelo",
                    error = formulario.errorModelo
                )
                CampoBapp(
                    valor = formulario.encargado,
                    alCambiar = viewModel::cambiarEncargado,
                    etiqueta = "Encargado",
                    error = formulario.errorEncargado
                )
                CampoBapp(
                    valor = formulario.capacidad,
                    alCambiar = viewModel::cambiarCapacidad,
                    etiqueta = "Capacidad",
                    error = formulario.errorCapacidad,
                    tipoTeclado = KeyboardType.Number
                )
            }
        },
        confirmButton = {
            TextButton(onClick = viewModel::guardar, enabled = !formulario.guardando) {
                Text(
                    text = if (formulario.guardando) "Guardando..." else "Guardar",
                    color = CianBorde,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = viewModel::cerrarFormulario) { Text("Cancelar", color = Color.White) }
        }
    )
}
