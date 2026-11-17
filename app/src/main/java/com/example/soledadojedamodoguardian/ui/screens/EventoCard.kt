package com.example.soledadojedamodoguardian.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.soledadojedamodoguardian.data.MonitorEntity
import com.example.soledadojedamodoguardian.ui.components.Amarillo
import com.example.soledadojedamodoguardian.ui.components.Etiqueta
import com.example.soledadojedamodoguardian.ui.components.Rojo
import com.example.soledadojedamodoguardian.ui.components.Verde
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun hora(fecha: Date): String {
    return SimpleDateFormat(
        "HH:mm",
        Locale("es", "CL")
    ).format(fecha)
}

@Composable
fun EventoCard(
    evento: MonitorEntity,
    onClick: () -> Unit
) {
    val colorPrioridad = when (evento.prioridad) {
        "Crítica" -> Rojo
        "Alta", "Media" -> Amarillo
        else -> Verde
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(colorPrioridad)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = evento.titulo,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = evento.zona,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "${hora(evento.fecha)} · " +
                            "${evento.camara} · ${evento.tipo}",
                    style = MaterialTheme.typography.labelSmall
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Etiqueta(
                        texto = evento.estado,
                        color = if (evento.estado == "Pendiente") {
                            Amarillo
                        } else {
                            Verde
                        }
                    )

                    Text(
                        text = evento.prioridad,
                        color = colorPrioridad,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

