package com.example.soledadojedamodoguardian.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.soledadojedamodoguardian.R
import com.example.soledadojedamodoguardian.model.Rol
import com.example.soledadojedamodoguardian.navigation.Screen
import com.example.soledadojedamodoguardian.viewmodel.LoginViewModel
import com.example.soledadojedamodoguardian.viewmodel.NavigationViewModel

@Composable
fun LoginScreen(
    vm: LoginViewModel,
    nav: NavigationViewModel
) {
    val state by vm.uiState.collectAsState()
    val errores by vm.errores.collectAsState()

    var visible by rememberSaveable {
        mutableStateOf(false)
    }

    var proveedor by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val violeta = Color(0xFF7C3AED)

    val colorEnlace = if (
        MaterialTheme.colorScheme.background.red < 0.5f
    ) {
        Color(0xFFC4A8FF)
    } else {
        violeta
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = 24.dp,
                        vertical = 24.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.logo_modo_guardian
                    ),
                    contentDescription = "Logo de Modo Guardián",
                    modifier = Modifier.size(150.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Bienvenido, inicia sesión",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = state.email,
                    onValueChange = vm::onEmailChange,
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
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

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
                        errores.passwordError?.let {
                            Text(it)
                        }
                    },
                    singleLine = true,
                    enabled = !state.isLoading,
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

                    Text(
                        text = "Mostrar contraseña",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (state.mensajeError.isNotEmpty()) {
                    Text(
                        text = state.mensajeError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
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
                    enabled = state.isLoginEnabled &&
                            !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = violeta,
                        contentColor = Color.White,
                        disabledContainerColor =
                            violeta.copy(alpha = 0.25f),
                        disabledContentColor =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text("Ingresando...")
                    } else {
                        Text(
                            text = "Iniciar sesión",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(44.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    Text(
                        text = "O CONTINUAR CON",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall
                    )

                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BotonProveedorLogin(
                        nombre = "Google",
                        logo = logoGoogleLogin,
                        conservarColores = true,
                        enabled = !state.isLoading,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            proveedor = "Google"
                        }
                    )

                    BotonProveedorLogin(
                        nombre = "Apple",
                        logo = logoAppleLogin,
                        conservarColores = false,
                        enabled = !state.isLoading,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            proveedor = "Apple"
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(
                    onClick = {
                        nav.navigateTo(Screen.Registro.route)
                    },
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant
                                )
                            ) {
                                append("¿No tienes cuenta? ")
                            }

                            withStyle(
                                SpanStyle(
                                    color = colorEnlace,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Crear cuenta")
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    proveedor?.let { nombre ->
        AlertDialog(
            onDismissRequest = {
                proveedor = null
            },
            title = {
                Text("Continuar con $nombre")
            },
            text = {
                Text(
                    "El acceso con $nombre aún no está conectado. " +
                            "Ingresa con tu correo y contraseña " +
                            "o crea una cuenta local."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        proveedor = null
                    }
                ) {
                    Text("Entendido")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        proveedor = null
                        nav.navigateTo(Screen.Registro.route)
                    }
                ) {
                    Text("Crear cuenta")
                }
            }
        )
    }
}

@Composable
private fun BotonProveedorLogin(
    nombre: String,
    logo: ImageVector,
    conservarColores: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = 50.dp),
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(
            horizontal = 12.dp,
            vertical = 12.dp
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        if (conservarColores) {
            Image(
                imageVector = logo,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                contentScale = ContentScale.Fit
            )
        } else {
            Icon(
                imageVector = logo,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = nombre,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun crearLogoLogin(
    nombre: String,
    rutas: List<Pair<Color, String>>
): ImageVector {
    val builder = ImageVector.Builder(
        name = nombre,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    )

    rutas.forEach { (color, ruta) ->
        builder.addPath(
            pathData = PathParser()
                .parsePathString(ruta)
                .toNodes(),
            fill = SolidColor(color)
        )
    }

    return builder.build()
}

private val logoGoogleLogin: ImageVector by lazy {
    crearLogoLogin(
        nombre = "Google",
        rutas = listOf(
            Color(0xFF4285F4) to
                    "M22.56 12.25c0-.73-.06-1.42-.19-2.09H12v3.96h5.92c-.26 1.28-1.04 2.36-2.21 3.09v2.57h3.58c2.09-1.92 3.27-4.76 3.27-8.12z",

            Color(0xFF34A853) to
                    "M12 23c2.97 0 5.46-.98 7.28-2.66l-3.58-2.57c-.98.66-2.24 1.06-3.7 1.06-2.86 0-5.29-1.93-6.16-4.53H2.15v2.65C3.96 20.53 7.69 23 12 23z",

            Color(0xFFFBBC05) to
                    "M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.27H2.15C1.42 8.69 1 10.3 1 12s.42 3.31 1.15 4.73l3.69-2.64z",

            Color(0xFFEA4335) to
                    "M12 5.38c1.62 0 3.07.56 4.21 1.64l3.16-3.16C17.45 2.09 14.97 1 12 1 7.69 1 3.96 3.47 2.15 7.27l3.69 2.64C6.71 7.31 9.14 5.38 12 5.38z"
        )
    )
}

private val logoAppleLogin: ImageVector by lazy {
    crearLogoLogin(
        nombre = "Apple",
        rutas = listOf(
            Color.White to
                    "M12.152 6.896c-.948 0-2.415-1.078-3.96-1.04-2.04.027-3.91 1.183-4.961 3.014-2.117 3.675-.546 9.103 1.519 12.09 1.013 1.454 2.208 3.09 3.792 3.039 1.52-.065 2.09-.987 3.935-.987 1.831 0 2.35.987 3.96.948 1.637-.026 2.676-1.48 3.676-2.948 1.156-1.688 1.636-3.325 1.662-3.415-.039-.013-3.182-1.221-3.22-4.857-.026-3.04 2.48-4.494 2.597-4.559-1.429-2.09-3.623-2.324-4.39-2.376-2-.156-3.675 1.09-4.61 1.09zM15.53 3.83c.843-1.012 1.4-2.427 1.245-3.83-1.207.052-2.662.805-3.532 1.818-.78.896-1.454 2.338-1.273 3.714 1.338.104 2.715-.688 3.559-1.701"
        )
    )
}