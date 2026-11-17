package com.example.soledadojedamodoguardian.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.soledadojedamodoguardian.data.MonitorEntity
import com.example.soledadojedamodoguardian.model.Rol
import com.example.soledadojedamodoguardian.model.Usuario
import com.example.soledadojedamodoguardian.ui.components.*
import com.example.soledadojedamodoguardian.viewmodel.MonitorViewModel
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AlertDetailContenido(
    evento: MonitorEntity,
    usuario: Usuario,
    vm: MonitorViewModel,
    onBack: () -> Unit,
    onReportar: () -> Unit,
    onCamera: () -> Unit
) {
    val context = LocalContext.current

    val acciones by remember(evento.id) {
        vm.acciones(evento.id)
    }.collectAsState(initial = emptyList())

    var ahora by remember {
        mutableLongStateOf(System.currentTimeMillis())
    }

    var confirmar by rememberSaveable(evento.id) {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(evento.id, evento.estado) {
        while (evento.estado == "Pendiente") {
            ahora = System.currentTimeMillis()
            delay(1000)
        }
    }

    val restante = (
            (evento.fecha.time + 120_000L - ahora) / 1000L
            ).coerceIn(0L, 120L)

    val formato = SimpleDateFormat(
        "dd/MM/yyyy HH:mm:ss",
        Locale("es", "CL")
    )

    Row {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Volver"
            )
        }

        Titulo(
            texto = if (evento.tipo == "Alerta") {
                "Detalle de alerta"
            } else {
                "Detalle de evento"
            }
        )
    }

    Etiqueta(
        texto = evento.prioridad.uppercase(),
        color = if (evento.prioridad == "Crítica") {
            Rojo
        } else {
            Amarillo
        }
    )

    Panel {
        Titulo(evento.titulo)

        FilaDato(
            titulo = "Ubicación",
            valor = evento.zona
        )

        FilaDato(
            titulo = "Fecha",
            valor = formato.format(evento.fecha)
        )

        FilaDato(
            titulo = "Estado",
            valor = evento.estado
        )

        Text(
            text = "Evento de prueba · Sin análisis de IA real",
            style = MaterialTheme.typography.bodySmall
        )
    }

    OutlinedButton(
        onClick = onCamera,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Outlined.Videocam,
            contentDescription = null
        )

        Text(" Ver cámara simulada")
    }

    Panel {
        Titulo("Detalles del evento")

        FilaDato(
            titulo = "Tipo",
            valor = evento.tipo
        )

        FilaDato(
            titulo = "Zona",
            valor = evento.zona
        )

        FilaDato(
            titulo = "Cámara",
            valor = evento.camara
        )

        FilaDato(
            titulo = "Duración referencial",
            valor = "12 segundos ·"
        )
    }

    evento.fotoPath?.let { ruta ->
        AsyncImage(
            model = File(ruta),
            contentDescription = "Evidencia de la incidencia",
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )
    }

    if (evento.tipo == "Alerta") {
        Titulo("Acciones requeridas")

        Button(
            onClick = { confirmar = "Confirmada" },
            enabled = evento.estado == "Pendiente",
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Rojo
            )
        ) {
            Text("Confirmar alerta · Central ")
        }

        OutlinedButton(
            onClick = { confirmar = "Falsa alarma" },
            enabled = evento.estado == "Pendiente" &&
                    usuario.rol != Rol.SUPERVISOR,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Falsa alarma")
        }

        if (usuario.rol == Rol.SUPERVISOR) {
            Text(
                text = "El operador o administrador puede " +
                        "descartar falsas alarmas.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    OutlinedButton(
        onClick = onReportar,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Outlined.PhotoCamera,
            contentDescription = null
        )

        Text(" Reportar incidencia con foto")
    }

    OutlinedButton(
        onClick = { confirmar = "Llamar" },
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Outlined.Phone,
            contentDescription = null
        )

        Text(" Llamar a central")
    }

    if (evento.tipo == "Alerta") {
        Panel {
            Text(
                text = "Protocolo de demostración",
                color = Rojo
            )

            Text(
                text = "Tiempo referencial de atención: 2 minutos. " +
                        "El envío y escalamiento a central ."
            )

            Text(
                text = when {
                    evento.estado != "Pendiente" ->
                        "Evento atendido"

                    restante == 0L ->
                        "Plazo vencido · Escalamiento"

                    else ->
                        "${restante / 60}:" +
                                (restante % 60)
                                    .toString()
                                    .padStart(2, '0') +
                                " restantes"
                }
            )

            LinearProgressIndicator(
                progress = restante.toFloat() / 120f,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    Titulo("Trazabilidad")

    if (acciones.isEmpty()) {
        Text("Sin acciones registradas")
    }

    acciones.forEach { accion ->
        Panel {
            Text(accion.accion)

            Text(
                text = "${accion.usuario}\n" +
                        formato.format(accion.fecha),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    confirmar?.let { accion ->
        val esLlamada = accion == "Llamar"

        AlertDialog(
            onDismissRequest = { confirmar = null },
            title = {
                Text(
                    if (esLlamada) {
                        "Central de prueba"
                    } else {
                        "¿Registrar $accion?"
                    }
                )
            },
            text = {
                Text(
                    if (esLlamada) {
                        "No hay un número de central configurado. " +
                                "Puedes abrir el marcador sin iniciar " +
                                "una llamada."
                    } else {
                        "Se guardará tu usuario, la fecha y el " +
                                "nuevo estado. No se enviarán datos " +
                                "a una central real."
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmar = null

                        if (esLlamada) {
                            try {
                                val intent = Intent(
                                    Intent.ACTION_DIAL,
                                    Uri.parse("tel:")
                                )

                                context.startActivity(intent)
                            } catch (e: Exception) {
                                vm.mensaje(
                                    "Marcador no disponible"
                                )
                            }
                        } else {
                            vm.atender(
                                evento = evento,
                                estado = accion,
                                email = usuario.correo,
                                rol = usuario.rol
                            )
                        }
                    }
                ) {
                    Text(
                        if (esLlamada) {
                            "Abrir marcador"
                        } else {
                            "Confirmar"
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { confirmar = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
