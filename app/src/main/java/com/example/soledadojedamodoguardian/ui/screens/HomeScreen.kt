package com.example.soledadojedamodoguardian.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.soledadojedamodoguardian.model.camarasDemo
import com.example.soledadojedamodoguardian.navigation.Screen
import com.example.soledadojedamodoguardian.ui.utils.escribirReporte
import com.example.soledadojedamodoguardian.viewmodel.LoginViewModel
import com.example.soledadojedamodoguardian.viewmodel.MonitorViewModel
import com.example.soledadojedamodoguardian.viewmodel.NavigationViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    vm: LoginViewModel,
    nav: NavigationViewModel,
    monitor: MonitorViewModel = viewModel()
) {
    val auth by vm.uiState.collectAsState()
    val usuario = auth.usuario

    if (usuario == null) {
        LaunchedEffect(Unit) {
            nav.navigateTo(Screen.Login.route)
        }
        return
    }

    val eventos by monitor.eventos.collectAsState()
    val estado by monitor.estado.collectAsState()

    var seccion by rememberSaveable {
        mutableStateOf("Inicio")
    }

    var detalleId by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var cameraId by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var incidencia by rememberSaveable {
        mutableStateOf(false)
    }

    var exportIds by rememberSaveable {
        mutableStateOf(arrayListOf<String>())
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    val exportar = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            val registros = eventos.filter {
                it.id in exportIds
            }

            scope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        context.contentResolver
                            .openOutputStream(uri)
                            .use { salida ->
                                requireNotNull(salida) {
                                    "No se pudo abrir el documento"
                                }

                                escribirReporte(
                                    salida = salida,
                                    eventos = registros,
                                    usuario = usuario.correo
                                )
                            }
                    }

                    monitor.mensaje("Reporte PDF guardado")
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    monitor.mensaje(
                        "No se pudo exportar el reporte"
                    )
                }
            }
        }
    }

    LaunchedEffect(estado.mensaje) {
        if (estado.mensaje.isNotEmpty()) {
            snackbar.showSnackbar(estado.mensaje)
            monitor.limpiarMensaje()
        }
    }

    fun principal(destino: String) {
        seccion = destino
        detalleId = null
        cameraId = null
        incidencia = false
    }

    BackHandler(
        enabled = detalleId != null ||
                cameraId != null ||
                incidencia ||
                seccion != "Inicio"
    ) {
        when {
            incidencia -> incidencia = false
            cameraId != null -> cameraId = null
            detalleId != null -> detalleId = null
            else -> principal("Inicio")
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbar)
        },
        bottomBar = {
            NavigationBar {
                val opciones = listOf(
                    "Inicio" to Icons.Outlined.Home,
                    "Cámaras" to Icons.Outlined.Videocam,
                    "Eventos" to Icons.Outlined.FormatListBulleted,
                    "Alertas" to Icons.Outlined.Notifications,
                    "Perfil" to Icons.Outlined.Person
                )

                opciones.forEach { (nombre, icono) ->
                    NavigationBarItem(
                        selected = seccion == nombre,
                        onClick = {
                            principal(nombre)
                        },
                        label = {
                            Text(
                                text = nombre,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        icon = {
                            if (nombre == "Alertas") {
                                BadgedBox(
                                    badge = {
                                        val pendientes = eventos.count {
                                            it.tipo == "Alerta" &&
                                                    it.estado == "Pendiente"
                                        }

                                        if (pendientes > 0) {
                                            Badge {
                                                Text(
                                                    pendientes.toString()
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = icono,
                                        contentDescription = nombre
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = icono,
                                    contentDescription = nombre
                                )
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            val scroll = rememberScrollState()

            LaunchedEffect(
                seccion,
                cameraId,
                detalleId,
                incidencia
            ) {
                scroll.scrollTo(0)
            }

            Column(
                modifier = Modifier
                    .widthIn(max = 1000.dp)
                    .fillMaxWidth()
                    .verticalScroll(scroll)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedVisibility(
                    visible = estado.cargando
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (estado.error.isNotEmpty()) {
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = estado.error,
                                color = MaterialTheme.colorScheme.error
                            )

                            Text(
                                "Puedes seguir consultando " +
                                        "los registros guardados."
                            )

                            TextButton(
                                onClick = {
                                    monitor.sincronizar()
                                }
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }

                val evento = eventos.find {
                    it.id == detalleId
                }

                val camera = camarasDemo.find {
                    it.id == cameraId
                }

                when {
                    incidencia -> {
                        IncidenciaContenido(
                            usuario = usuario,
                            vm = monitor,
                            onBack = {
                                incidencia = false
                            }
                        )
                    }

                    camera != null -> {
                        CameraContenido(
                            camera = camera,
                            onBack = {
                                cameraId = null
                            },
                            mensaje = monitor::mensaje
                        )
                    }

                    evento != null -> {
                        AlertDetailContenido(
                            evento = evento,
                            usuario = usuario,
                            vm = monitor,
                            onBack = {
                                detalleId = null
                            },
                            onReportar = {
                                incidencia = true
                            },
                            onCamera = {
                                cameraId = camarasDemo.find {
                                    it.id == evento.camara
                                }?.id

                                if (cameraId == null) {
                                    monitor.mensaje(
                                        "Este reporte no tiene " +
                                                "una cámara asociada"
                                    )
                                }
                            }
                        )
                    }

                    seccion == "Inicio" -> {
                        InicioContenido(
                            usuario = usuario,
                            eventos = eventos,
                            vm = monitor,
                            onCamara = {
                                cameraId = it.id
                                seccion = "Cámaras"
                            },
                            onTodas = {
                                principal("Cámaras")
                            },
                            onEventos = {
                                principal("Eventos")
                            },
                            onAlerta = {
                                detalleId = it.id
                                seccion = if (it.tipo == "Alerta") {
                                    "Alertas"
                                } else {
                                    "Eventos"
                                }
                            },
                            onReportar = {
                                incidencia = true
                            }
                        )
                    }

                    seccion == "Cámaras" -> {
                        CamarasContenido(
                            onCamara = {
                                cameraId = it.id
                            }
                        )
                    }

                    seccion == "Eventos" ||
                            seccion == "Alertas" -> {
                        EventosContenido(
                            eventos = eventos,
                            soloAlertas = seccion == "Alertas",
                            onDetalle = {
                                detalleId = it.id
                            },
                            onExportar = { lista ->
                                exportIds = ArrayList(
                                    lista.map { it.id }
                                )

                                exportar.launch(
                                    "ModoGuardian_reporte.pdf"
                                )
                            }
                        )
                    }

                    else -> {
                        PerfilContenido(
                            usuario = usuario,
                            vm = monitor,
                            onCameras = {
                                principal("Cámaras")
                            },
                            onEventos = {
                                principal("Eventos")
                            },
                            onLogout = {
                                vm.cerrarSesion()
                                nav.navigateTo(
                                    Screen.Login.route
                                )
                            }
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }
        }
    }
}