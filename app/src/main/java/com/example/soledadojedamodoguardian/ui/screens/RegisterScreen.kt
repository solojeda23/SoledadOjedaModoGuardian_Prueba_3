package com.example.soledadojedamodoguardian.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

import com.example.soledadojedamodoguardian.viewmodel.LoginViewModel
import com.example.soledadojedamodoguardian.viewmodel.NavigationViewModel


@Composable
fun RegisterScreen(
    vm: LoginViewModel,
    nav: NavigationViewModel
) {


    val state by vm.uiState.collectAsState()
    val errores by vm.errores.collectAsState()

    var visible by remember {
        mutableStateOf(false)
    }


    var mensaje by remember {
        mutableStateOf("")
    }

    Scaffold { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {


            Text(
                text = "Crear cuenta",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))


            OutlinedTextField(
                value = state.nombre,
                onValueChange = {
                    vm.onNombreChange(it)
                    mensaje = ""
                },
                label = {
                    Text("Nombre de usuario")
                },
                isError = errores.nombreError != null,
                supportingText = {
                    errores.nombreError?.let {
                        Text(it)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )


            OutlinedTextField(
                value = state.email,
                onValueChange = {
                    vm.onEmailChange(it)
                    mensaje = ""
                },
                label = {
                    Text("Correo electrónico")
                },
                isError = errores.emailError != null,
                supportingText = {
                    errores.emailError?.let {
                        Text(it)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )


            OutlinedTextField(
                value = state.password,
                onValueChange = {
                    vm.onPasswordChange(it)
                    mensaje = ""
                },
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
                    errores.passwordError?.let {
                        Text(it)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )


            OutlinedTextField(
                value = state.confirmarPassword,
                onValueChange = {
                    vm.onConfirmarPasswordChange(it)
                    mensaje = ""
                },
                label = {
                    Text("Confirmar contraseña")
                },
                visualTransformation = if (visible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                isError = errores.confirmarPasswordError != null,
                supportingText = {
                    errores.confirmarPasswordError?.let {
                        Text(it)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )


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

            Spacer(modifier = Modifier.height(8.dp))


            if (state.mensajeError.isNotEmpty()) {
                Text(
                    text = state.mensajeError,
                    color = MaterialTheme.colorScheme.error
                )
            }


            if (mensaje.isNotEmpty()) {
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            Button(
                enabled = !state.isLoading,
                onClick = {
                    mensaje = ""

                    vm.registrar { registrado ->

                        if (registrado) {
                            mensaje = "Cuenta creada correctamente"
                        } else {
                            if (
                                vm.uiState.value.mensajeError.isEmpty()
                            ) {
                                mensaje =
                                    "Revisa los datos del formulario"
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = if (state.isLoading) {
                        "Creando cuenta..."
                    } else {
                        "Crear cuenta"
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                enabled = !state.isLoading,
                onClick = {
                    nav.navigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al inicio de sesión")
            }
        }
    }
}