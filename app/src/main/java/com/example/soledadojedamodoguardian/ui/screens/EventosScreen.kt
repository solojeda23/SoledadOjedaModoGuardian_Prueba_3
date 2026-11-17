package com.example.soledadojedamodoguardian.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.soledadojedamodoguardian.data.MonitorEntity
import com.example.soledadojedamodoguardian.ui.components.Amarillo
import com.example.soledadojedamodoguardian.ui.components.FilaDato
import com.example.soledadojedamodoguardian.ui.components.Panel
import com.example.soledadojedamodoguardian.ui.components.Rojo
import com.example.soledadojedamodoguardian.ui.components.Titulo
import com.example.soledadojedamodoguardian.ui.components.Verde
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun EventosContenido(
    eventos: List<MonitorEntity>,
    soloAlertas: Boolean,
    onDetalle: (MonitorEntity) -> Unit,
    onExportar: (List<MonitorEntity>) -> Unit
) {
    var tipo by rememberSaveable(soloAlertas) {
        mutableStateOf("Todos")
    }

    var busqueda by rememberSaveable(soloAlertas) {
        mutableStateOf("")
    }

    var fecha by rememberSaveable(soloAlertas) {
        mutableStateOf("Todos")
    }

    var critica by rememberSaveable(soloAlertas) {
        mutableStateOf(false)
    }

    var mostrarFiltros by rememberSaveable(soloAlertas) {
        mutableStateOf(false)
    }

    var camara by rememberSaveable(soloAlertas) {
        mutableStateOf(false)
    }

    var perimetro by rememberSaveable(soloAlertas) {
        mutableStateOf(false)
    }

    var estadoAlerta by rememberSaveable(soloAlertas) {
        mutableStateOf("Pendientes")
    }

    val base = if (soloAlertas) {
        eventos.filter { it.tipo == "Alerta" }
    } else {
        eventos
    }

    val pendientes = base.filter {
        it.estado == "Pendiente"
    }

    val atendidos = base.count {
        it.estado != "Pendiente"
    }

    val criticasPendientes = pendientes.count {
        it.prioridad == "Crítica"
    }

    val filtrosActivos = listOf(
        critica,
        camara,
        perimetro,
        !soloAlertas && tipo != "Todos",
        soloAlertas && fecha != "Todos"
    ).count { it }

    val formatoDia = SimpleDateFormat(
        "yyyyMMdd",
        Locale.ROOT
    )

    val hoy = formatoDia.format(Date())

    val calendarioAyer = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -1)
    }

    val ayer = formatoDia.format(calendarioAyer.time)

    val tipoSeleccionado = when (tipo) {
        "Alertas" -> "Alerta"
        "Eventos" -> "Evento"
        "Accesos" -> "Acceso"
        else -> null
    }

    val filtrados = base.filter { evento ->
        val coincideTipo = soloAlertas ||
                tipoSeleccionado == null ||
                evento.tipo == tipoSeleccionado

        val coincidePrioridad = !critica ||
                evento.prioridad == "Crítica"

        val coincideCamara = !camara ||
                evento.camara == "CAM-01"

        val coincideZona = !perimetro ||
                evento.zona.contains(
                    "Perímetro",
                    ignoreCase = true
                )

        val diaEvento = formatoDia.format(evento.fecha)

        val coincideFecha = when (fecha) {
            "Hoy" -> diaEvento == hoy
            "Ayer" -> diaEvento == ayer
            else -> true
        }

        val coincideEstado = if (soloAlertas) {
            when (estadoAlerta) {
                "Pendientes" -> evento.estado == "Pendiente"
                "Atendidas" -> evento.estado != "Pendiente"
                else -> true
            }
        } else {
            true
        }

        val textoBusqueda =
            "${evento.titulo} ${evento.zona} " +
                    "${evento.camara} ${evento.tipo}"

        val coincideBusqueda = textoBusqueda.contains(
            busqueda.trim(),
            ignoreCase = true
        )

        coincideTipo &&
                coincidePrioridad &&
                coincideCamara &&
                coincideZona &&
                coincideFecha &&
                coincideEstado &&
                coincideBusqueda
    }

    val ordenUrgencia =
        compareBy<MonitorEntity> {
            if (it.estado == "Pendiente") 0 else 1
        }.thenBy {
            when (it.prioridad) {
                "Crítica" -> 0
                "Alta" -> 1
                "Media" -> 2
                else -> 3
            }
        }.thenByDescending {
            it.fecha.time
        }

    if (soloAlertas) {
        Titulo(
            texto = "Centro de alertas",
            detalle = "Prioriza y registra la atención"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ResumenAlerta(
                titulo = "PENDIENTES",
                cantidad = pendientes.size,
                color = Amarillo,
                modifier = Modifier.weight(1f)
            )

            ResumenAlerta(
                titulo = "CRÍTICAS",
                cantidad = criticasPendientes,
                color = Rojo,
                modifier = Modifier.weight(1f)
            )

            ResumenAlerta(
                titulo = "ATENDIDAS",
                cantidad = atendidos,
                color = Verde,
                modifier = Modifier.weight(1f)
            )
        }

        val colorAviso = if (pendientes.isNotEmpty()) {
            Amarillo
        } else {
            Verde
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = colorAviso.copy(alpha = 0.08f),
            border = BorderStroke(
                1.dp,
                colorAviso.copy(alpha = 0.4f)
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = colorAviso,
                        modifier = Modifier.size(28.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (pendientes.isNotEmpty()) {
                                "Hay alertas por atender"
                            } else {
                                "Todo al día"
                            },
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = if (pendientes.isNotEmpty()) {
                                "Revisa primero las de mayor prioridad."
                            } else {
                                "No tienes alertas pendientes. " +
                                        "Puedes consultar las atendidas."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (pendientes.isNotEmpty()) {
                    Button(
                        onClick = {
                            pendientes
                                .sortedWith(ordenUrgencia)
                                .firstOrNull()
                                ?.let(onDetalle)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Atender la más urgente")
                    }
                }
            }
        }

        SelectorOrdenado(
            opciones = listOf(
                "Pendientes",
                "Atendidas",
                "Todas"
            ),
            seleccion = estadoAlerta,
            onSeleccion = { estadoAlerta = it }
        )
    } else {
        Titulo(
            texto = "Historial",
            detalle = "Actividad, accesos y registros del turno"
        )

        Panel {
            FilaDato(
                titulo = "Registros totales",
                valor = base.size.toString()
            )

            FilaDato(
                titulo = "Críticas pendientes",
                valor = criticasPendientes.toString()
            )

            FilaDato(
                titulo = "Registros atendidos",
                valor = atendidos.toString()
            )

            Text(
                text = "PROGRESO DEL TURNO · 62% REFERENCIAL",
                style = MaterialTheme.typography.labelSmall
            )

            LinearProgressIndicator(
                progress = 0.62f,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp),
            placeholder = {
                Text(
                    if (soloAlertas) {
                        "Buscar una alerta"
                    } else {
                        "Buscar cámara, zona o evento"
                    }
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor =
                    MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor =
                    MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor =
                    MaterialTheme.colorScheme.primary,
                unfocusedBorderColor =
                    MaterialTheme.colorScheme.outlineVariant
            )
        )

        if (!soloAlertas) {
            Text(
                text = "FECHA",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SelectorOrdenado(
                opciones = listOf("Todos", "Hoy", "Ayer"),
                seleccion = fecha,
                onSeleccion = { fecha = it }
            )
        }

        OutlinedButton(
            onClick = { mostrarFiltros = true },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Tune,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (filtrosActivos > 0) {
                    "Filtrar · $filtrosActivos activos"
                } else {
                    "Filtrar registros"
                }
            )
        }

        val resumen = buildList<String> {
            if (!soloAlertas && tipo != "Todos") {
                add(tipo)
            }

            if (soloAlertas && fecha != "Todos") {
                add(fecha)
            }

            if (critica) {
                add("Solo críticas")
            }

            if (camara) {
                add("Cámara 01")
            }

            if (perimetro) {
                add("Perímetro")
            }
        }

        if (resumen.isNotEmpty()) {
            Text(
                text = resumen.joinToString(" · "),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (soloAlertas) {
                estadoAlerta
            } else {
                "Registros encontrados"
            },
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "${filtrados.size} registros",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
    }

    if (soloAlertas) {
        filtrados
            .sortedWith(ordenUrgencia)
            .forEach { alerta ->
                EventoCard(
                    evento = alerta,
                    onClick = { onDetalle(alerta) }
                )
            }
    } else {
        val formatoGrupo = SimpleDateFormat(
            "dd/MM · HH':00'",
            Locale("es", "CL")
        )

        filtrados
            .sortedByDescending { it.fecha.time }
            .groupBy { formatoGrupo.format(it.fecha) }
            .forEach { (grupo, registros) ->
                Text(
                    text = "$grupo · ${registros.size} registros",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium
                )

                registros.forEach { evento ->
                    EventoCard(
                        evento = evento,
                        onClick = { onDetalle(evento) }
                    )
                }
            }
    }

    if (filtrados.isEmpty()) {
        Panel {
            Text(
                text = if (
                    soloAlertas &&
                    estadoAlerta == "Pendientes" &&
                    pendientes.isEmpty()
                ) {
                    "No tienes alertas pendientes."
                } else {
                    "No hay registros con esta selección."
                }
            )

            TextButton(
                onClick = {
                    tipo = "Todos"
                    fecha = "Todos"
                    busqueda = ""
                    critica = false
                    camara = false
                    perimetro = false

                    if (soloAlertas) {
                        estadoAlerta = "Todas"
                    }
                }
            ) {
                Text("Mostrar todos los registros")
            }
        }
    }

    Button(
        onClick = { onExportar(filtrados) },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Download,
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            if (soloAlertas) {
                "Exportar alertas PDF"
            } else {
                "Exportar reporte PDF"
            }
        )
    }

    if (mostrarFiltros) {
        AlertDialog(
            onDismissRequest = {
                mostrarFiltros = false
            },
            title = {
                Text(
                    if (soloAlertas) {
                        "Filtrar alertas"
                    } else {
                        "Filtrar historial"
                    }
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 440.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (soloAlertas) {
                        Text(
                            text = "FECHA",
                            fontWeight = FontWeight.Bold
                        )

                        listOf(
                            "Todos",
                            "Hoy",
                            "Ayer"
                        ).forEach { opcion ->
                            OpcionUnica(
                                texto = opcion,
                                seleccionada = fecha == opcion,
                                onClick = {
                                    fecha = opcion
                                }
                            )
                        }
                    } else {
                        Text(
                            text = "TIPO DE REGISTRO",
                            fontWeight = FontWeight.Bold
                        )

                        listOf(
                            "Todos",
                            "Alertas",
                            "Eventos",
                            "Accesos"
                        ).forEach { opcion ->
                            OpcionUnica(
                                texto = opcion,
                                seleccionada = tipo == opcion,
                                onClick = {
                                    tipo = opcion
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "OPCIONES ADICIONALES",
                        fontWeight = FontWeight.Bold
                    )

                    OpcionMultiple(
                        texto = "Solo alertas críticas",
                        seleccionada = critica,
                        onCambio = {
                            critica = it
                        }
                    )

                    OpcionMultiple(
                        texto = "Solo Cámara 01",
                        seleccionada = camara,
                        onCambio = {
                            camara = it
                        }
                    )

                    OpcionMultiple(
                        texto = "Solo zonas de perímetro",
                        seleccionada = perimetro,
                        onCambio = {
                            perimetro = it
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarFiltros = false
                    }
                ) {
                    Text("Ver resultados")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        tipo = "Todos"
                        fecha = "Todos"
                        busqueda = ""
                        critica = false
                        camara = false
                        perimetro = false
                        mostrarFiltros = false
                    }
                ) {
                    Text("Limpiar filtros")
                }
            }
        )
    }
}

@Composable
private fun SelectorOrdenado(
    opciones: List<String>,
    seleccion: String,
    onSeleccion: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        opciones.forEach { opcion ->
            val seleccionada = seleccion == opcion

            Surface(
                onClick = {
                    onSeleccion(opcion)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                color = if (seleccionada) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                contentColor = if (seleccionada) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                border = BorderStroke(
                    1.dp,
                    if (seleccionada) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    }
                )
            ) {
                Box(
                    modifier = Modifier
                        .heightIn(min = 50.dp)
                        .padding(
                            horizontal = 6.dp,
                            vertical = 12.dp
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = opcion,
                        fontWeight = if (seleccionada) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Medium
                        },
                        style = MaterialTheme.typography.bodySmall,
                        textAlign =
                            androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ResumenAlerta(
    titulo: String,
    cantidad: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.08f),
        border = BorderStroke(
            1.dp,
            color.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = cantidad.toString(),
                color = color,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = titulo,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun OpcionUnica(
    texto: String,
    seleccionada: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = seleccionada,
            onClick = onClick
        )

        Text(texto)
    }
}

@Composable
private fun OpcionMultiple(
    texto: String,
    seleccionada: Boolean,
    onCambio: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onCambio(!seleccionada)
            }
            .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = seleccionada,
            onCheckedChange = onCambio
        )

        Text(texto)
    }
}