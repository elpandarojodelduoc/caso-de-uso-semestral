package com.example.bapptest.ui.components

package com.example.bapptest.ui.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bapptest.R
import com.example.bapptest.ui.theme.CampoClaro
import com.example.bapptest.ui.theme.CianBorde
import com.example.bapptest.ui.theme.ColorError
import com.example.bapptest.ui.theme.FondoApp
import com.example.bapptest.ui.theme.TarjetaAzul
import com.example.bapptest.ui.theme.Turquesa
import com.example.bapptest.util.FechaUtil
import com.example.bapptest.util.ImagenUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PantallaBapp(
    modifier: Modifier = Modifier,
    desplazable: Boolean = true,
    arregloVertical: Arrangement.Vertical = Arrangement.Top,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .systemBarsPadding()
            .imePadding()
            .padding(12.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(36.dp))
                .background(TarjetaAzul)
                .then(if (desplazable) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                .padding(horizontal = 22.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = arregloVertical,
            content = contenido
        )
    }
}

@Composable
fun EncabezadoBapp(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.logo_cmc),
            contentDescription = "Logo CMC Ciberseguridad OT",
            modifier = Modifier.height(72.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = "BAPP",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TituloPantalla(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        color = Color.White,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
    )
}

@Composable
private fun coloresCampo() = TextFieldDefaults.colors(
    focusedContainerColor = CampoClaro,
    unfocusedContainerColor = CampoClaro,
    disabledContainerColor = CampoClaro,
    errorContainerColor = CampoClaro,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    errorTextColor = Color.White,
    cursorColor = Color.White,
    errorCursorColor = Color.White,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    errorIndicatorColor = Color.Transparent,
    focusedTrailingIconColor = Color.Black,
    unfocusedTrailingIconColor = Color.Black,
    errorTrailingIconColor = Color.Black
)

@Composable
fun CampoBapp(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    soloLectura: Boolean = false,
    esClave: Boolean = false,
    claveVisible: Boolean = false,
    alAlternarClave: (() -> Unit)? = null,
    iconoFinal: (@Composable () -> Unit)? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text
) {
    val iconoClave: @Composable () -> Unit = {
        IconButton(onClick = { alAlternarClave?.invoke() }) {
            Icon(
                painter = painterResource(if (claveVisible) R.drawable.ic_ojo else R.drawable.ic_ojo_tachado),
                contentDescription = if (claveVisible) "Ocultar contraseña" else "Mostrar contraseña",
                tint = Color.Black
            )
        }
    }
    val iconoFinalEfectivo = if (esClave && alAlternarClave != null) iconoClave else iconoFinal

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = etiqueta,
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        TextField(
            value = valor,
            onValueChange = alCambiar,
            readOnly = soloLectura,
            singleLine = true,
            isError = error != null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            textStyle = TextStyle(fontSize = 18.sp),
            visualTransformation = if (esClave && !claveVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = if (esClave) KeyboardType.Password else tipoTeclado),
            trailingIcon = iconoFinalEfectivo,
            colors = coloresCampo()
        )
        AnimatedVisibility(visible = error != null) {
            Text(
                text = error.orEmpty(),
                color = ColorError,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun BotonBapp(
    texto: String,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier,
    icono: (@Composable () -> Unit)? = null,
    habilitado: Boolean = true,
    colorFondo: Color = Turquesa,
    subrayado: Boolean = false,
    borde: BorderStroke? = null
) {
    Button(
        onClick = alPulsar,
        enabled = habilitado,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorFondo,
            contentColor = Color.White,
            disabledContainerColor = colorFondo.copy(alpha = 0.45f),
            disabledContentColor = Color.White.copy(alpha = 0.6f)
        ),
        border = borde
    ) {
        if (icono != null) {
            icono()
            Spacer(Modifier.width(10.dp))
        }
        Text(
            text = texto,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            textDecoration = if (subrayado) TextDecoration.Underline else null
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorFecha(
    fechaInicial: String,
    alSeleccionar: (String) -> Unit,
    alCerrar: () -> Unit
) {
    val estado = rememberDatePickerState(
        initialSelectedDateMillis = FechaUtil.aMillis(fechaInicial),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                utcTimeMillis <= FechaUtil.hoyEnMillis()
        }
    )
    DatePickerDialog(
        onDismissRequest = alCerrar,
        confirmButton = {
            TextButton(
                onClick = {
                    estado.selectedDateMillis?.let { alSeleccionar(FechaUtil.desdeMillis(it)) }
                    alCerrar()
                }
            ) { Text("Aceptar") }
        },
        dismissButton = { TextButton(onClick = alCerrar) { Text("Cancelar") } }
    ) {
        DatePicker(state = estado)
    }
}

@Composable
fun DialogoConfirmacion(
    titulo: String,
    mensaje: String,
    textoConfirmar: String,
    alConfirmar: () -> Unit,
    alCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = alCerrar,
        containerColor = TarjetaAzul,
        titleContentColor = Color.White,
        textContentColor = Color.White,
        title = { Text(titulo, fontWeight = FontWeight.Bold) },
        text = { Text(mensaje) },
        confirmButton = {
            TextButton(onClick = alConfirmar) { Text(textoConfirmar, color = CianBorde, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = alCerrar) { Text("Cancelar", color = Color.White) }
        }
    )
}

@Composable
fun DialogoInformativo(
    titulo: String,
    mensaje: String,
    textoBoton: String,
    alCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = alCerrar,
        containerColor = TarjetaAzul,
        titleContentColor = Color.White,
        textContentColor = Color.White,
        title = { Text(titulo, fontWeight = FontWeight.Bold) },
        text = { Text(mensaje) },
        confirmButton = {
            TextButton(onClick = alCerrar) { Text(textoBoton, color = CianBorde, fontWeight = FontWeight.Bold) }
        }
    )
}

@Composable
fun rememberImagen(uri: Uri?): State<ImageBitmap?> {
    val contexto = LocalContext.current
    return produceState<ImageBitmap?>(initialValue = null, key1 = uri) {
        value = if (uri == null) {
            null
        } else {
            withContext(Dispatchers.IO) { ImagenUtil.cargarImagen(contexto, uri) }
        }
    }
}
