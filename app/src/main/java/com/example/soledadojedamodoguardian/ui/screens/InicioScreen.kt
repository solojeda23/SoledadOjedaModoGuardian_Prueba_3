package com.example.soledadojedamodoguardian.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soledadojedamodoguardian.data.MonitorEntity
import com.example.soledadojedamodoguardian.model.Camera
import com.example.soledadojedamodoguardian.model.Usuario
import com.example.soledadojedamodoguardian.model.camarasDemo
import com.example.soledadojedamodoguardian.ui.components.*
import com.example.soledadojedamodoguardian.viewmodel.MonitorViewModel

@Composable
fun InicioContenido(
    usuario: Usuario,
    eventos: List<MonitorEntity>,
    vm: MonitorViewModel,
    onCamara: (Camera) -> Unit,
    onTodas: () -> Unit,
    onEventos: () -> Unit,
    onAlerta: (MonitorEntity) -> Unit,
    onReportar: () -> Unit
) {
    val pendientes = eventos.filter {
        it.tipo == "Alerta" && it.estado == "Pendiente"
    }

    val total = camarasDemo.size
    val activas = camarasDemo.count { it.online }
    val desconectadas = total - activas

    val capacidad = if (total > 0) {
        activas.toFloat() / total
    } else {
        0f
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "TU CENTRO DE MONITOREO",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )

            Text(
                text = "Hola, ${usuario.nombre}",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 32.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "${usuario.rol} · Zona Sur",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }

        FilledTonalIconButton(
            onClick = {
                val primera = pendientes.firstOrNull()

                if (primera != null) {
                    onAlerta(primera)
                } else {
                    vm.mensaje("No tienes alertas pendientes")
                }
            },
            modifier = Modifier.size(48.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor =
                    MaterialTheme.colorScheme.primaryContainer,
                contentColor =
                    MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            BadgedBox(
                badge = {
                    if (pendientes.isNotEmpty()) {
                        Badge {
                            Text(pendientes.size.toString())
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Ver alerta pendiente"
                )
            }
        }
    }


    Panel {
        Titulo(
            texto = "Estado General",
            detalle = " MODO GUARDIÁN"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "TOTAL",
                    style = MaterialTheme.typography.labelSmall
                )

                Text(
                    text = total.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Cámaras",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "ACTIVAS",
                    color = Verde,
                    style = MaterialTheme.typography.labelSmall
                )

                Text(
                    text = activas.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "En línea",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "ATENCIÓN",
                    color = Amarillo,
                    style = MaterialTheme.typography.labelSmall
                )

                Text(
                    text = desconectadas.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Sin conexión",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Text(
            text = "CAPACIDAD OPERATIVA · " +
                    "${(capacidad * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall
        )

        LinearProgressIndicator(
            progress = capacidad,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Datos y dispositivos",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )

        TextButton(
            onClick = { vm.sincronizar() }
        ) {
            Text("Sincronizar datos")
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Cámaras asignadas",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )

        TextButton(onClick = onTodas) {
            Text("Ver todas")
        }
    }

    CuadriculaCamaras(
        camaras = camarasDemo.take(4),
        onCamara = onCamara
    )

    Titulo(texto = "Eventos recientes")

    if (eventos.isEmpty()) {
        Text("No hay eventos guardados")
    }

    eventos
        .sortedByDescending { it.fecha.time }
        .take(4)
        .forEach { evento ->
            EventoCard(
                evento = evento,
                onClick = { onAlerta(evento) }
            )
        }

    TextButton(
        onClick = onEventos,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Ver historial completo")
    }

    Panel {
        FilaDato(
            titulo = "Protocolo activo",
            valor = "Nivel 2 ·"
        )

        Text(
            text = "Revisa las alertas y registra su atención.",
            style = MaterialTheme.typography.bodySmall
        )
    }

    Button(
        onClick = onReportar,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Warning,
            contentDescription = null
        )

        Text(" Reportar incidencia")
    }
}