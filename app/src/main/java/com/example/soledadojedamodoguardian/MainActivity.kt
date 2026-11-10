package com.example.soledadojedamodoguardian

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.soledadojedamodoguardian.navigation.NavigationEvent
import com.example.soledadojedamodoguardian.navigation.Screen
import com.example.soledadojedamodoguardian.ui.screens.LoginScreen
import com.example.soledadojedamodoguardian.ui.screens.RegisterScreen
import com.example.soledadojedamodoguardian.ui.screens.HomeScreen
import com.example.soledadojedamodoguardian.viewmodel.LoginViewModel
import com.example.soledadojedamodoguardian.viewmodel.NavigationViewModel

import kotlinx.coroutines.flow.collect

class MainActivity : ComponentActivity() {

    // ViewModels compartidos entre todas las pantallas
    private val loginVm: LoginViewModel by viewModels()
    private val navVm: NavigationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                // Controlador de navegación
                val navController = rememberNavController()

                // Escuchar eventos del NavigationViewModel
                LaunchedEffect(navController) {

                    navVm.events.collect { event ->

                        when (event) {

                            is NavigationEvent.NavigateTo -> {

                                navController.navigate(event.route) {

                                    // Limpiar pantallas anteriores
                                    // al entrar o cerrar sesión
                                    if (event.route !=
                                        Screen.Registro.route
                                    ) {
                                        popUpTo(navController.graph.id) {
                                            inclusive = false
                                        }
                                    }

                                    launchSingleTop = true
                                }
                            }

                            NavigationEvent.NavigateBack -> {
                                navController.popBackStack()
                            }
                        }
                    }
                }

                // Pantallas disponibles de la aplicación
                NavHost(
                    navController = navController,
                    startDestination = Screen.Login.route
                ) {

                    // Pantalla de inicio de sesión
                    composable(Screen.Login.route) {
                        LoginScreen(loginVm, navVm)
                    }

                    // Pantalla de registro
                    composable(Screen.Registro.route) {
                        RegisterScreen(loginVm, navVm)
                    }

                    // Pantalla Administrador
                    composable(Screen.HomeAdmin.route) {
                        HomeScreen(loginVm, navVm)
                    }

                    // Pantalla Supervisor
                    composable(Screen.HomeSupervisor.route) {
                        HomeScreen(loginVm, navVm)
                    }

                    // Pantalla Operador
                    composable(Screen.HomeOperador.route) {
                        HomeScreen(loginVm, navVm)
                    }
                }
            }
        }
    }
}