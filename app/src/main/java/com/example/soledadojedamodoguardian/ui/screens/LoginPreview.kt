package com.example.soledadojedamodoguardian.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.soledadojedamodoguardian.R

// Vista previa para celular
@Preview(
    name = "Celular - Compact",
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
fun LoginPreviewCompact() {
    LoginPreviewContenido()
}

// Vista previa para pantalla mediana
@Preview(
    name = "Tablet - Medium",
    showBackground = true,
    widthDp = 700,
    heightDp = 900
)
@Composable
fun LoginPreviewMedium() {
    LoginPreviewContenido(pantallaGrande = true)
}

// Vista previa para pantalla grande
@Preview(
    name = "Pantalla grande - Expanded",
    showBackground = true,
    widthDp = 1000,
    heightDp = 800
)
@Composable
fun LoginPreviewExpanded() {
    LoginPreviewContenido(pantallaGrande = true)
}

// Representación visual del login para las previews.
// No utiliza Room ni realiza autenticación.
@Composable
private fun LoginPreviewContenido(
    pantallaGrande: Boolean = false
) {
    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val formulario: @Composable () -> Unit = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(
                            R.drawable.logo_modo_guardian
                        ),
                        contentDescription = "Logo Modo Guardián",
                        modifier = Modifier.size(150.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Modo Guardián",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Bienvenido, inicia sesión")

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        label = { Text("Contraseña") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = false,
                            onCheckedChange = {}
                        )

                        Text("Mostrar contraseña")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Iniciar sesión")
                    }

                    TextButton(onClick = {}) {
                        Text("¿No tienes cuenta? Regístrate")
                    }
                }
            }

            if (pantallaGrande) {
                Card(
                    modifier = Modifier
                        .widthIn(max = 480.dp)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    )
                ) {
                    formulario()
                }
            } else {
                formulario()
            }
        }
    }
}