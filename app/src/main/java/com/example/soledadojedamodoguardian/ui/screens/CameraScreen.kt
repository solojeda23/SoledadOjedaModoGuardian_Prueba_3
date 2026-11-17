package com.example.soledadojedamodoguardian.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.soledadojedamodoguardian.model.Camera
import com.example.soledadojedamodoguardian.ui.components.*
import kotlinx.coroutines.delay

@Composable
fun CameraContenido(
    camera: Camera,
    onBack: () -> Unit,
    mensaje: (String) -> Unit
) {
    var grabando by rememberSaveable(camera.id) {
        mutableStateOf(false)
    }

    var hablar by rememberSaveable(camera.id) {
        mutableStateOf(false)
    }

    var luz by rememberSaveable(camera.id) {
        mutableStateOf(false)
    }

    var full by rememberSaveable(camera.id) {
        mutableStateOf(false)
    }

    var segundos by rememberSaveable(camera.id) {
        mutableIntStateOf(0)
    }

    var mostrarInfo by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(grabando) {
        while (grabando) {
            delay(1000)
            segundos++
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Volver"
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = camera.nombre,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "${camera.id} · VISTA",
                style = MaterialTheme.typography.labelSmall
            )
        }

        IconButton(
            onClick = { mostrarInfo = true }
        ) {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = "Información de cámara"
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (full) 560.dp else 300.dp)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        if (luz) {
                            Color(0xFF24513E)
                        } else {
                            Color(0xFF0C3028)
                        },
                        Color(0xFF060D10)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {
        Text(
            text = "${camera.id}\nIR · AUTO",
            color = Verde
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (camera.online) {
                    Icons.Outlined.Videocam
                } else {
                    Icons.Outlined.VideocamOff
                },
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = Verde
            )

            Text(
                text = if (camera.online) {
                    "EN VIVO"
                } else {
                    "Dispositivo sin conexión"
                },
                color = Color.White
            )


        }

        Text(
            text = if (grabando) {
                "● REC  · ${segundos}s"
            } else {
                "● VISTA PREVIA"
            },
            modifier = Modifier.align(Alignment.BottomStart),
            color = if (grabando) Rojo else Verde
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ControlCamara(
            texto = "Foto",
            icono = Icons.Outlined.PhotoCamera
        ) {
            mensaje(
                if (camera.online) {
                    "Captura  de ${camera.id}. " +
                            "Para una foto real usa Reportar incidencia"
                } else {
                    "Cámara sin conexión"
                }
            )
        }

        ControlCamara(
            texto = if (grabando) "Parar" else "Grabar",
            icono = if (grabando) {
                Icons.Outlined.Stop
            } else {
                Icons.Outlined.FiberManualRecord
            },
            activo = grabando
        ) {
            if (camera.online) {
                grabando = !grabando

                if (grabando) {
                    segundos = 0
                }

                mensaje(
                    if (grabando) {
                        "Grabación iniciada"
                    } else {
                        "Grabación detenida"
                    }
                )
            } else {
                mensaje("Cámara sin conexión")
            }
        }

        ControlCamara(
            texto = if (hablar) "Silenciar" else "Hablar",
            icono = if (hablar) {
                Icons.Outlined.Mic
            } else {
                Icons.Outlined.MicOff
            },
            activo = hablar
        ) {
            if (camera.online) {
                hablar = !hablar

                mensaje(
                    if (hablar) {
                        "Audio activado"
                    } else {
                        "Audio desactivado"
                    }
                )
            } else {
                mensaje("Cámara sin conexión")
            }
        }

        ControlCamara(
            texto = "Luz",
            icono = Icons.Outlined.FlashlightOn,
            activo = luz
        ) {
            if (camera.online) {
                luz = !luz

                mensaje(
                    if (luz) {
                        "Luz encendida"
                    } else {
                        "Luz apagada"
                    }
                )
            } else {
                mensaje("Cámara sin conexión")
            }
        }

        ControlCamara(
            texto = if (full) "Reducir" else "Full",
            icono = if (full) {
                Icons.Outlined.FullscreenExit
            } else {
                Icons.Outlined.Fullscreen
            },
            activo = full
        ) {
            full = !full
        }
    }

    if (!full) {
        Panel {
            Titulo("Estado del dispositivo")

            FilaDato(
                titulo = "Estado",
                valor = if (camera.online) {
                    "En línea"
                } else {
                    "Sin conexión"
                }
            )

            FilaDato(
                titulo = "Señal",
                valor = if (camera.online) {
                    "Excelente ·"
                } else {
                    "Sin señal"
                }
            )

            FilaDato(
                titulo = "Grabación",
                valor = if (grabando) {
                    " Activa"
                } else {
                    "Detenida"
                }
            )

            FilaDato(
                titulo = "Ubicación",
                valor = camera.zona
            )
        }
    }

    if (mostrarInfo) {
        AlertDialog(
            onDismissRequest = { mostrarInfo = false },
            title = { Text(camera.nombre) },
            text = {
                Text(
                    "${camera.id}\n${camera.zona}\n\n" +
                            "Cámara simulada para la prueba académica. " +
                            "No transmite ni guarda video o audio real."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { mostrarInfo = false }
                ) {
                    Text("Entendido")
                }
            }
        )
    }
}

@Composable
private fun ControlCamara(
    texto: String,
    icono: ImageVector,
    activo: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.width(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilledTonalIconButton(
            onClick = onClick,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = if (activo) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                contentColor = if (activo) {
                    Color.White
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        ) {
            Icon(
                imageVector = icono,
                contentDescription = texto
            )
        }

        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

