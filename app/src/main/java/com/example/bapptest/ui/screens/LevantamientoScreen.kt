package com.example.bapptest.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bapptest.R
import com.example.bapptest.ui.components.BotonBapp
import com.example.bapptest.ui.components.CampoBapp
import com.example.bapptest.ui.components.EncabezadoBapp
import com.example.bapptest.ui.components.PantallaBapp
import com.example.bapptest.ui.components.SelectorFecha
import com.example.bapptest.ui.components.TituloPantalla
import com.example.bapptest.ui.theme.CampoClaro
import com.example.bapptest.util.ImagenUtil
import com.example.bapptest.viewmodel.LevantamientoViewModel
import com.example.bapptest.viewmodel.ROLES_DISPONIBLES

@Composable
fun LevantamientoScreen(
    viewModel: LevantamientoViewModel,
    alElegirImagen: () -> Unit,
    alVolver: () -> Unit
) {
    val contexto = LocalContext.current
    val borrador = viewModel.borrador
    var mostrarSelectorFecha by remember { mutableStateOf(false) }
    var menuRolAbierto by remember { mutableStateOf(false) }
    var uriFotoPendiente by rememberSaveable { mutableStateOf<Uri?>(null) }

    val lanzadorCamara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { fotoTomada ->
        val uriFoto = uriFotoPendiente
        if (fotoTomada && uriFoto != null) {
            viewModel.establecerImagen(uriFoto, contexto.contentResolver.getType(uriFoto))
            alElegirImagen()
        }
    }

    PantallaBapp {
        EncabezadoBapp()
        TituloPantalla("Levantamiento de Rack")
        CampoBapp(
            valor = borrador.idRack,
            alCambiar = viewModel::cambiarIdRack,
            etiqueta = "ID del Rack:",
            error = viewModel.errorIdRack
        )
        Spacer(Modifier.height(10.dp))
        Box {
            CampoBapp(
                valor = borrador.fecha,
                alCambiar = {},
                etiqueta = "Fecha:",
                error = viewModel.errorFecha,
                soloLectura = true,
                iconoFinal = {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                }
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { mostrarSelectorFecha = true }
            )
        }
        Spacer(Modifier.height(10.dp))
        Box {
            CampoBapp(
                valor = borrador.rol,
                alCambiar = {},
                etiqueta = "Rol:",
                soloLectura = true,
                iconoFinal = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Seleccionar rol")
                }
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { menuRolAbierto = true }
            )
            DropdownMenu(expanded = menuRolAbierto, onDismissRequest = { menuRolAbierto = false }) {
                ROLES_DISPONIBLES.forEach { rol ->
                    DropdownMenuItem(
                        text = { Text(rol) },
                        onClick = {
                            viewModel.cambiarRol(rol)
                            menuRolAbierto = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(28.dp))
        BotonBapp(
            texto = "Capturar fotografía",
            alPulsar = {
                if (viewModel.validarFormulario()) {
                    val uriNueva = ImagenUtil.crearUriFotoTemporal(contexto)
                    uriFotoPendiente = uriNueva
                    try {
                        lanzadorCamara.launch(uriNueva)
                    } catch (e: Exception) {
                        Toast.makeText(contexto, "No hay una aplicación de cámara disponible", Toast.LENGTH_LONG).show()
                    }
                }
            },
            icono = {
                Icon(painterResource(R.drawable.ic_camara), contentDescription = "Cámara")
            }
        )
        Spacer(Modifier.height(14.dp))
        BotonBapp(
            texto = "Seleccionar imagen",
            alPulsar = {
                if (viewModel.validarFormulario()) alElegirImagen()
            },
            icono = {
                Icon(painterResource(R.drawable.ic_imagen), contentDescription = "Galería")
            }
        )
        Spacer(Modifier.height(14.dp))
        BotonBapp(
            texto = "Regresar el menú principal",
            alPulsar = alVolver,
            colorFondo = CampoClaro,
            icono = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar") }
        )
    }

    if (mostrarSelectorFecha) {
        SelectorFecha(
            fechaInicial = borrador.fecha,
            alSeleccionar = viewModel::cambiarFecha,
            alCerrar = { mostrarSelectorFecha = false }
        )
    }
}
