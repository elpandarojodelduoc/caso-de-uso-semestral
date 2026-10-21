package com.example.bapptest.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bapptest.ui.screens.CargaScreen
import com.example.bapptest.ui.screens.CatalogoScreen
import com.example.bapptest.ui.screens.LevantamientoScreen
import com.example.bapptest.ui.screens.LoginScreen
import com.example.bapptest.ui.screens.MenuScreen
import com.example.bapptest.ui.screens.ProcesamientoScreen
import com.example.bapptest.ui.screens.RacksScreen
import com.example.bapptest.ui.screens.RegistroScreen
import com.example.bapptest.ui.screens.RevisionScreen
import com.example.bapptest.ui.screens.SubirImagenScreen
import com.example.bapptest.viewmodel.AuthViewModel
import com.example.bapptest.viewmodel.LevantamientoViewModel
import com.example.bapptest.viewmodel.RacksViewModel

object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val CARGA = "carga"
    const val MENU = "menu"
    const val RACKS = "racks"
    const val LEVANTAMIENTO = "levantamiento"
    const val SUBIR_IMAGEN = "subir_imagen"
    const val PROCESAMIENTO = "procesamiento"
    const val CATALOGO = "catalogo"
    const val REVISION = "revision"
}

private const val DURACION_TRANSICION_MS = 350

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    levantamientoViewModel: LevantamientoViewModel,
    racksViewModel: RacksViewModel
) {
    val navController = rememberNavController()
    val correoSesion = authViewModel.correoSesion.orEmpty()

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN,
        enterTransition = {
            fadeIn(tween(DURACION_TRANSICION_MS)) + slideInHorizontally(tween(DURACION_TRANSICION_MS)) { it / 8 }
        },
        exitTransition = { fadeOut(tween(DURACION_TRANSICION_MS)) },
        popEnterTransition = { fadeIn(tween(DURACION_TRANSICION_MS)) },
        popExitTransition = {
            fadeOut(tween(DURACION_TRANSICION_MS)) + slideOutHorizontally(tween(DURACION_TRANSICION_MS)) { it / 8 }
        }
    ) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                alIrARegistro = { navController.navigate(Rutas.REGISTRO) },
                alIniciarSesion = { navController.navigate(Rutas.CARGA) }
            )
        }
        composable(Rutas.REGISTRO) {
            RegistroScreen(
                viewModel = authViewModel,
                alVolver = { navController.popBackStack() }
            )
        }
        composable(Rutas.CARGA) {
            CargaScreen(
                alTerminar = {
                    navController.navigate(Rutas.MENU) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Rutas.MENU) {
            MenuScreen(
                correoUsuario = correoSesion,
                alLevantarRack = {
                    levantamientoViewModel.reiniciar()
                    navController.navigate(Rutas.LEVANTAMIENTO)
                },
                alVerRacks = { navController.navigate(Rutas.RACKS) },
                alCerrarSesion = {
                    authViewModel.cerrarSesion()
                    levantamientoViewModel.reiniciar()
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.MENU) { inclusive = true }
                    }
                }
            )
        }
        composable(Rutas.RACKS) {
            RacksScreen(
                viewModel = racksViewModel,
                correoUsuario = correoSesion,
                alAbrirRack = { rackId ->
                    levantamientoViewModel.cargarRack(rackId) {
                        navController.navigate(Rutas.REVISION)
                    }
                },
                alVolver = { navController.popBackStack() }
            )
        }
        composable(Rutas.LEVANTAMIENTO) {
            LevantamientoScreen(
                viewModel = levantamientoViewModel,
                alElegirImagen = { navController.navigate(Rutas.SUBIR_IMAGEN) },
                alVolver = { navController.popBackStack() }
            )
        }
        composable(Rutas.SUBIR_IMAGEN) {
            SubirImagenScreen(
                viewModel = levantamientoViewModel,
                alSubir = { navController.navigate(Rutas.PROCESAMIENTO) },
                alVolver = { navController.popBackStack() }
            )
        }
        composable(Rutas.PROCESAMIENTO) {
            ProcesamientoScreen(
                viewModel = levantamientoViewModel,
                alIrACatalogo = { navController.navigate(Rutas.CATALOGO) },
                alVolverAlMenu = {
                    levantamientoViewModel.reiniciar()
                    navController.popBackStack(Rutas.MENU, inclusive = false)
                }
            )
        }
        composable(Rutas.CATALOGO) {
            CatalogoScreen(
                viewModel = levantamientoViewModel,
                alEditarRegistro = { navController.navigate(Rutas.REVISION) },
                alVolver = { navController.popBackStack() }
            )
        }
        composable(Rutas.REVISION) {
            RevisionScreen(
                viewModel = levantamientoViewModel,
                correoUsuario = correoSesion,
                alGuardar = {
                    levantamientoViewModel.reiniciar()
                    navController.navigate(Rutas.RACKS) {
                        popUpTo(Rutas.MENU)
                    }
                },
                alVolverACatalogo = {
                    if (!navController.popBackStack(Rutas.CATALOGO, inclusive = false)) {
                        navController.navigate(Rutas.CATALOGO)
                    }
                }
            )
        }
    }
}
