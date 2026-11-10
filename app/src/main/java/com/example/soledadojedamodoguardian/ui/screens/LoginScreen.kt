package com.example.soledadojedamodoguardian.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

import com.example.soledadojedamodoguardian.R
import com.example.soledadojedamodoguardian.model.Rol
import com.example.soledadojedamodoguardian.navigation.Screen
import com.example.soledadojedamodoguardian.viewmodel.LoginViewModel
import com.example.soledadojedamodoguardian.viewmodel.NavigationViewModel
import com.example.soledadojedamodoguardian.ui.utils.WindowSize
import com.example.soledadojedamodoguardian.ui.utils.calcularWindowSize

// Pantalla de inicio de sesión de Modo Guardián
@Composable
fun LoginScreen(
    vm: LoginViewModel,
    nav: NavigationViewModel
) {
    val state by vm.uiState.collectAsState()
    val errores by vm.errores.collectAsState()

    // Control para mostrar u ocultar contraseña
    var visible by remember {
        mutableStateOf(false)
    }

    // Detectar tamaño de pantalla
    val windowSize = calcularWindowSize()
    val esCompacto = windowSize == WindowSize.COMPACT

    Scaffold { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {

            val contenido: @Composable () -> Unit = {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    // Logo de los dos perros salchicha
                    Image(
                        painter = painterResource(
                            id = R.drawable.logo_modo_guardian
                        ),
                        contentDescription = "Logo de Modo Guardián con dos perros salchicha",
                        modifier = Modifier.size(180.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Nombre de la aplicación
                    Text(
                        text = "Modo Guardián",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Bienvenido, inicia sesión",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Campo correo electrónico
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = vm::onEmailChange,
                        label = {
                            Text("Correo electrónico")
                        },
                        isError = errores.emailError != null,
                        supportingText = {
                            errores.emailError?.let { error ->
                                Text(error)
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Campo contraseña
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = vm::onPasswordChange,
                        label = {
                            Text("Contraseña")
                        },
                        visualTransformation = if (visible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        isError = errores.passwordError != null,
                        supportingText = {
                            errores.passwordError?.let { error ->
                                Text(error)
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Mostrar u ocultar contraseña
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = visible,
                            onCheckedChange = {
                                visible = it
                            }
                        )

                        Text("Mostrar contraseña")
                    }

                    // Mostrar mensaje de error
                    if (state.mensajeError.isNotEmpty()) {
                        Text(
                            text = state.mensajeError,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón de inicio de sesión
                    Button(
                        enabled = state.isLoginEnabled &&
                                !state.isLoading,
                        onClick = {

                            vm.iniciarSesion { exitoso ->

                                if (exitoso) {

                                    val ruta = when (
                                        vm.uiState.value.usuario?.rol
                                    ) {
                                        Rol.ADMIN ->
                                            Screen.HomeAdmin.route

                                        Rol.SUPERVISOR ->
                                            Screen.HomeSupervisor.route

                                        Rol.OPERADOR ->
                                            Screen.HomeOperador.route

                                        else ->
                                            Screen.Login.route
                                    }

                                    nav.navigateTo(ruta)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = if (state.isLoading) {
                                "Iniciando sesión..."
                            } else {
                                "Iniciar sesión"
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Ir a la pantalla de registro
                    TextButton(
                        onClick = {
                            nav.navigateTo(Screen.Registro.route)
                        }
                    ) {
                        Text("¿No tienes cuenta? Regístrate")
                    }
                }
            }

            // Diseño para teléfonos
            if (esCompacto) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    contenido()
                }

            } else {

                // Diseño para pantallas grandes
                Card(
                    modifier = Modifier
                        .widthIn(max = 480.dp)
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    )
                ) {
                    contenido()
                }
            }
        }
    }
}