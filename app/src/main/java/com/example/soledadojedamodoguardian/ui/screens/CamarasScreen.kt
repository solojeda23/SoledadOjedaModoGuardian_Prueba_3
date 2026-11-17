package com.example.soledadojedamodoguardian.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.outlined.VideocamOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.soledadojedamodoguardian.model.Camera
import com.example.soledadojedamodoguardian.model.camarasDemo
import com.example.soledadojedamodoguardian.ui.components.Amarillo
import com.example.soledadojedamodoguardian.ui.components.Rojo
import com.example.soledadojedamodoguardian.ui.components.Titulo
import com.example.soledadojedamodoguardian.ui.components.Verde
import com.example.soledadojedamodoguardian.ui.utils.WindowSize
import com.example.soledadojedamodoguardian.ui.utils.calcularWindowSize

@Composable
fun CamarasContenido(
    onCamara: (Camera) -> Unit
) {
    var busqueda by rememberSaveable {
        mutableStateOf("")
    }

    var soloOnline by rememberSaveable {
        mutableStateOf(false)
    }

    val total = camarasDemo.size
    val conectadas = camarasDemo.count { it.online }
    val desconectadas = total - conectadas

    Titulo(
        texto = "Centro de cámaras",
        detalle = "Supervisión de dispositivos · Demostración"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        IndicadorCamara(
            titulo = "ASIGNADAS",
            cantidad = total,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )

        IndicadorCamara(
            titulo = "EN LÍNEA",
            cantidad = conectadas,
            color = Verde,
            modifier = Modifier.weight(1f)
        )

        IndicadorCamara(
            titulo = "SIN SEÑAL",
            cantidad = desconectadas,
            color = Amarillo,
            modifier = Modifier.weight(1f)
        )
    }

    OutlinedTextField(
        value = busqueda,
        onValueChange = { busqueda = it },
        label = {
            Text("Buscar cámara, ubicación o código")
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )

    val filtradas = camarasDemo.filter { camara ->
        val coincideBusqueda =
            "${camara.nombre} ${camara.zona} ${camara.id}"
                .contains(busqueda, ignoreCase = true)

        val coincideEstado = !soloOnline || camara.online

        coincideBusqueda && coincideEstado
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = soloOnline,
            onClick = {
                soloOnline = !soloOnline
            },
            label = {
                Text("Solo en línea")
            },
            shape = RoundedCornerShape(8.dp),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor =
                    MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor =
                    MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        Text(
            text = "${filtradas.size} resultados",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (filtradas.isEmpty()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = "No hay cámaras que coincidan con tu búsqueda.",
                modifier = Modifier.padding(20.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    CuadriculaCamaras(
        camaras = filtradas,
        onCamara = onCamara
    )
}

@Composable
private fun IndicadorCamara(
    titulo: String,
    cantidad: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = cantidad.toString(),
                color = color,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = titulo,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

// Conserva el nombre para que Inicio siga funcionando.
@Composable
fun CuadriculaCamaras(
    camaras: List<Camera>,
    onCamara: (Camera) -> Unit
) {
    val columnas = when (calcularWindowSize()) {
        WindowSize.COMPACT -> 1
        WindowSize.MEDIUM -> 2
        WindowSize.EXPANDED -> 2
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        camaras.chunked(columnas).forEach { grupo ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                grupo.forEach { camara ->
                    TarjetaCamara(
                        camara = camara,
                        onClick = {
                            onCamara(camara)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                repeat(columnas - grupo.size) {
                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaCamara(
    camara: Camera,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(82.dp)
                    .height(108.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF292E33),
                                Color(0xFF121619)
                            )
                        )
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = if (camara.online) {
                        "● REC ·"
                    } else {
                        "OFFLINE"
                    },
                    color = if (camara.online) {
                        Rojo
                    } else {
                        Color(0xFFB6BEC5)
                    },
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.TopStart)
                )

                Icon(
                    imageVector = if (camara.online) {
                        Icons.Outlined.Videocam
                    } else {
                        Icons.Outlined.VideocamOff
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(30.dp),
                    tint = if (camara.online) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color(0xFF90999F)
                    }
                )

                Text(
                    text = camara.id,
                    modifier = Modifier.align(
                        Alignment.BottomStart
                    ),
                    color = Color(0xFFE7EDF1),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = camara.nombre,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = camara.zona,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = if (camara.online) {
                        "● En línea"
                    } else {
                        "● Sin conexión"
                    },
                    color = if (camara.online) {
                        Verde
                    } else {
                        Amarillo
                    },
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = if (camara.online) {
                        "Abrir cámara"
                    } else {
                        "Ver estado"
                    },
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
