package com.example.soledadojedamodoguardian.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.soledadojedamodoguardian.navigation.Screen
import com.example.soledadojedamodoguardian.viewmodel.LoginViewModel
import com.example.soledadojedamodoguardian.viewmodel.NavigationViewModel

// Pantalla principal después de iniciar sesión
@Composable
fun HomeScreen(
    vm: LoginViewModel,
    nav: NavigationViewModel
) {

    // Obtener la información del usuario
    val state by vm.uiState.collectAsState()

    val usuario = state.usuario

    Scaffold { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Título de bienvenida
            Text(
                text = "Bienvenido a Modo Guardián",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Nombre del usuario
            Text(
                text = "Usuario: ${usuario?.nombre ?: ""}"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Correo electrónico
            Text(
                text = "Correo: ${usuario?.correo ?: ""}"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Rol del usuario
            Text(
                text = "Rol: ${usuario?.rol ?: ""}"
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Botón para cerrar sesión
            Button(
                onClick = {

                    // Limpiar los datos del usuario
                    vm.cerrarSesion()

                    // Volver a la pantalla de login
                    nav.navigateTo(
                        Screen.Login.route
                    )
                },

                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}