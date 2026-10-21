package com.example.bapptest.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaColoresBapp = darkColorScheme(
    primary = Turquesa,
    onPrimary = Color.White,
    secondary = CampoClaro,
    onSecondary = Color.White,
    background = FondoApp,
    onBackground = Color.White,
    surface = TarjetaAzul,
    onSurface = Color.White,
    surfaceContainerHigh = TarjetaAzul,
    onSurfaceVariant = Color.White,
    error = ColorError
)

@Composable
fun BappTestTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaColoresBapp,
        typography = Typography,
        content = content
    )
}
