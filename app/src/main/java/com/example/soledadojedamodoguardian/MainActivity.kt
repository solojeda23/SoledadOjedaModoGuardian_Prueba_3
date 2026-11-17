package com.example.soledadojedamodoguardian

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.soledadojedamodoguardian.navigation.NavigationEvent
import com.example.soledadojedamodoguardian.navigation.Screen
import com.example.soledadojedamodoguardian.ui.screens.HomeScreen
import com.example.soledadojedamodoguardian.ui.screens.LoginScreen
import com.example.soledadojedamodoguardian.ui.screens.RegisterScreen
import com.example.soledadojedamodoguardian.ui.theme.SoledadOjedaModoGuardianTheme
import com.example.soledadojedamodoguardian.viewmodel.LoginViewModel
import com.example.soledadojedamodoguardian.viewmodel.MonitorViewModel
import com.example.soledadojedamodoguardian.viewmodel.NavigationViewModel

class MainActivity : ComponentActivity() {

    private val loginVm: LoginViewModel by viewModels()
    private val navVm: NavigationViewModel by viewModels()
    private val monitorVm: MonitorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val preferencias by monitorVm.preferencias.collectAsState()

            val modoOscuro = preferencias[
                booleanPreferencesKey("oscuro")
            ] ?: true

            SoledadOjedaModoGuardianTheme(
                darkTheme = modoOscuro
            ) {
                val navController = rememberNavController()

                LaunchedEffect(navController) {
                    navVm.events.collect { event ->
                        when (event) {
                            is NavigationEvent.NavigateTo -> {
                                navController.navigate(event.route) {

                                    if (
                                        event.route != Screen.Registro.route
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

                NavHost(
                    navController = navController,
                    startDestination = Screen.Login.route
                ) {
                    composable(Screen.Login.route) {
                        LoginScreen(
                            vm = loginVm,
                            nav = navVm
                        )
                    }

                    composable(Screen.Registro.route) {
                        RegisterScreen(
                            vm = loginVm,
                            nav = navVm
                        )
                    }

                    composable(Screen.HomeAdmin.route) {
                        HomeScreen(
                            vm = loginVm,
                            nav = navVm,
                            monitor = monitorVm
                        )
                    }

                    composable(Screen.HomeSupervisor.route) {
                        HomeScreen(
                            vm = loginVm,
                            nav = navVm,
                            monitor = monitorVm
                        )
                    }

                    composable(Screen.HomeOperador.route) {
                        HomeScreen(
                            vm = loginVm,
                            nav = navVm,
                            monitor = monitorVm
                        )
                    }
                }
            }
        }
    }
}