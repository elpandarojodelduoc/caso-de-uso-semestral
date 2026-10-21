package com.example.bapptest

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bapptest.navigation.AppNavigation
import com.example.bapptest.ui.theme.BappTestTheme
import com.example.bapptest.viewmodel.AuthViewModel
import com.example.bapptest.viewmodel.LevantamientoViewModel
import com.example.bapptest.viewmodel.RacksViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            BappTestTheme {
                val authViewModel: AuthViewModel = viewModel()
                val levantamientoViewModel: LevantamientoViewModel = viewModel()
                val racksViewModel: RacksViewModel = viewModel()
                AppNavigation(
                    authViewModel = authViewModel,
                    levantamientoViewModel = levantamientoViewModel,
                    racksViewModel = racksViewModel
                )
            }
        }
    }
}
