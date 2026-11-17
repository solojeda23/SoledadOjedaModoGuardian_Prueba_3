package com.example.soledadojedamodoguardian.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.example.soledadojedamodoguardian.model.Usuario
import com.example.soledadojedamodoguardian.ui.components.SelectorFoto
import com.example.soledadojedamodoguardian.ui.components.Titulo
import com.example.soledadojedamodoguardian.viewmodel.MonitorViewModel

@Composable
fun IncidenciaContenido(
    usuario: Usuario,
    vm: MonitorViewModel,
    onBack: () -> Unit
) {
    var titulo by rememberSaveable {
        mutableStateOf("")
    }

    var zona by rememberSaveable {
        mutableStateOf("")
    }

    var foto by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var validar by rememberSaveable {
        mutableStateOf(false)
    }

    var guardando by rememberSaveable {
        mutableStateOf(false)
    }

    val descripcionInvalida = titulo.trim().length < 5
    val zonaInvalida = zona.isBlank()

    Titulo(
        texto = "Reportar incidencia",
        detalle = "El reporte y la foto se guardarán en este dispositivo"
    )

    SelectorFoto(
        vm = vm,
        foto = foto,
        onFoto = { foto = it }
    )

    OutlinedTextField(
        value = titulo,
        onValueChange = { titulo = it },
        label = {
            Text("Descripción de la incidencia")
        },
        modifier = Modifier.fillMaxWidth(),
        minLines = 2,
        enabled = !guardando,
        isError = validar && descripcionInvalida,
        supportingText = {
            if (validar && descripcionInvalida) {
                Text("Escribe al menos 5 caracteres")
            }
        }
    )

    OutlinedTextField(
        value = zona,
        onValueChange = { zona = it },
        label = {
            Text("Zona / ubicación")
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        enabled = !guardando,
        isError = validar && zonaInvalida,
        supportingText = {
            if (validar && zonaInvalida) {
                Text("Ingresa la ubicación")
            }
        }
    )

    if (validar && foto == null) {
        Text(
            text = "Adjunta una foto desde Cámara o Galería",
            color = MaterialTheme.colorScheme.error
        )
    }

    Button(
        onClick = {
            validar = true

            if (
                !descripcionInvalida &&
                !zonaInvalida &&
                foto != null
            ) {
                guardando = true

                vm.reportar(
                    titulo = titulo,
                    zona = zona,
                    foto = foto,
                    email = usuario.correo,
                    onGuardado = {
                        guardando = false
                        onBack()
                    }
                ).invokeOnCompletion {
                    guardando = false
                }
            }
        },
        enabled = !guardando,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            if (guardando) {
                "Guardando..."
            } else {
                "Guardar incidencia"
            }
        )
    }

    TextButton(
        onClick = onBack,
        enabled = !guardando,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Volver")
    }
}

