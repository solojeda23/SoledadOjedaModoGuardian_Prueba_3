package com.example.soledadojedamodoguardian.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.example.soledadojedamodoguardian.model.Rol
import com.example.soledadojedamodoguardian.model.Usuario
import com.example.soledadojedamodoguardian.ui.components.*
import com.example.soledadojedamodoguardian.ui.utils.notificarPrueba
import com.example.soledadojedamodoguardian.viewmodel.MonitorViewModel

@Composable
fun PerfilContenido(
    usuario: Usuario,
    vm: MonitorViewModel,
    onCameras: () -> Unit,
    onEventos: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    val perfil by remember(usuario.correo) {
        vm.perfil(usuario.correo)
    }.collectAsState(initial = null)

    val preferencias by vm.preferencias.collectAsState()
    val eventos by vm.eventos.collectAsState()
    val estado by vm.estado.collectAsState()
    val usuarios by vm.usuarios.collectAsState()

    var direccion by rememberSaveable(
        usuario.correo,
        perfil?.direccion
    ) {
        mutableStateOf(perfil?.direccion ?: "")
    }

    var foto by rememberSaveable(
        usuario.correo,
        perfil?.fotoPath
    ) {
        mutableStateOf(perfil?.fotoPath)
    }

    var dialogo by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var actual by rememberSaveable {
        mutableStateOf("")
    }

    var nueva by rememberSaveable {
        mutableStateOf("")
    }

    val permisoNotificacion = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { autorizado ->
        val enviada = autorizado && notificarPrueba(context)

        vm.mensaje(
            if (enviada) {
                "Notificación de prueba enviada"
            } else {
                "Notificaciones no autorizadas. Revisa Ajustes de Android."
            }
        )
    }

    Titulo(
        texto = "Mi Perfil",
        detalle = "Cuenta y preferencias del turno"
    )

    Panel {
        SelectorFoto(
            vm = vm,
            foto = foto,
            onFoto = { foto = it }
        )

        Titulo(
            texto = usuario.nombre,
            detalle = usuario.correo
        )

        Etiqueta("${usuario.rol} · Zona Sur")

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección / zona asignada") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                vm.guardarPerfil(
                    email = usuario.correo,
                    direccion = direccion,
                    foto = foto
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar perfil")
        }
    }

    Panel {
        FilaDato("Turno referencial", "14:00 – 22:00")
        FilaDato("Cámaras", "12 asignadas")

        FilaDato(
            "Alertas atendidas",
            eventos.count {
                it.tipo == "Alerta" && it.estado != "Pendiente"
            }.toString()
        )
    }

    Text("MI CUENTA")

    AccionFila(
        texto = "Mis cámaras asignadas",
        detalle = "12 cámaras",
        onClick = onCameras
    )

    AccionFila(
        texto = "Historial de turnos",
        detalle = "Consultar registros locales",
        onClick = onEventos
    )

    AccionFila(
        texto = "Certificado / Credencial",
        detalle = "Documento de demostración"
    ) {
        dialogo = "credencial"
    }

    Text("SEGURIDAD")

    AccionFila("Cambiar contraseña") {
        actual = ""
        nueva = ""
        dialogo = "password"
    }

    AccionFila(
        texto = "Sesiones activas",
        detalle = "1 dispositivo local"
    ) {
        dialogo = "sesiones"
    }

    val opciones = listOf(
        "dosPasos" to "Verificación en 2 pasos ·",
        "push" to "Notificaciones Push",
        "criticas" to "Alertas críticas · Preferencia",
        "oscuro" to "Modo oscuro"
    )

    opciones.forEach { (clave, titulo) ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = titulo,
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = preferencias[
                    booleanPreferencesKey(clave)
                ] ?: (clave != "dosPasos"),
                onCheckedChange = {
                    vm.preferencia(clave, it)
                }
            )
        }
    }

    OutlinedButton(
        onClick = {
            val pushActivo = preferencias[
                booleanPreferencesKey("push")
            ] ?: true

            if (!pushActivo) {
                vm.mensaje("Activa Notificaciones Push primero")
            } else if (Build.VERSION.SDK_INT >= 33) {
                permisoNotificacion.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            } else {
                vm.mensaje(
                    if (notificarPrueba(context)) {
                        "Notificación de prueba enviada"
                    } else {
                        "Habilita las notificaciones en Ajustes de Android"
                    }
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Probar notificación")
    }

    Text("ESTADOS Y ANIMACIONES")

    val modoEspecial = preferencias[
        booleanPreferencesKey("modoEspecial")
    ] ?: false

    val textoDerivado by remember(modoEspecial) {
        derivedStateOf {
            if (modoEspecial) {
                "Modo especial activado"
            } else {
                "Activar modo especial"
            }
        }
    }

    val colorModo by animateColorAsState(
        targetValue = if (modoEspecial) {
            Verde
        } else {
            MaterialTheme.colorScheme.primary
        },
        label = "Color del modo especial"
    )

    Button(
        onClick = {
            vm.preferencia("modoEspecial", !modoEspecial)
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorModo
        )
    ) {
        Text(textoDerivado)
    }

    AnimatedVisibility(visible = modoEspecial) {
        Panel {
            Text("¡Beneficios Premium activados!")
            Text("Modo de demostración guardado con DataStore")
        }
    }

    Text("APP")

    AccionFila("Ayuda y soporte ") {
        dialogo = "ayuda"
    }

    AccionFila("Términos y políticas de seguridad") {
        dialogo = "terminos"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = "Simular falta de conexión",
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = estado.sinConexion,
            onCheckedChange = vm::conexion
        )
    }

    OutlinedButton(
        onClick = { vm.sincronizar() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Probar sincronización")
    }

    if (usuario.rol == Rol.ADMIN) {
        Panel {
            Titulo(
                texto = "Administrar usuarios",
                detalle = "Cuentas registradas en Room"
            )

            if (usuarios.isEmpty()) {
                Text("Crea una cuenta para verla aquí")
            }

            usuarios.forEach { cuenta ->
                Text("${cuenta.nombre}\n${cuenta.email}")

                Text(
                    text = "Rol actual: ${cuenta.rol}",
                    style = MaterialTheme.typography.bodySmall
                )

                Rol.entries.forEach { rol ->
                    OutlinedButton(
                        onClick = {
                            vm.cambiarRol(
                                id = cuenta.id,
                                rol = rol,
                                solicitante = usuario.rol
                            )
                        },
                        enabled = cuenta.rol != rol.name,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Asignar ${rol.name}")
                    }
                }
            }
        }
    }

    Button(
        onClick = { dialogo = "salir" },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Text("Cerrar sesión")
    }

    Text(
        text = "Modo Guardián · Seguridad y monitoreo",
        style = MaterialTheme.typography.bodySmall
    )

    dialogo?.let { tipo ->
        val tituloDialogo = when (tipo) {
            "password" -> "Cambiar contraseña"
            "salir" -> "¿Cerrar sesión?"
            "credencial" -> "Credencial de demostración"
            "sesiones" -> "Sesiones activas"
            "terminos" -> "Términos y privacidad"
            else -> "Ayuda y soporte"
        }

        AlertDialog(
            onDismissRequest = { dialogo = null },
            title = { Text(tituloDialogo) },
            text = {
                if (tipo == "password") {
                    Column {
                        OutlinedTextField(
                            value = actual,
                            onValueChange = { actual = it },
                            label = { Text("Contraseña actual") },
                            visualTransformation =
                                PasswordVisualTransformation()
                        )

                        OutlinedTextField(
                            value = nueva,
                            onValueChange = { nueva = it },
                            label = { Text("Nueva contraseña · mínimo 6") },
                            visualTransformation =
                                PasswordVisualTransformation()
                        )
                    }
                } else {
                    Text(
                        when (tipo) {
                            "salir" ->
                                "Se cerrará tu sesión en este dispositivo."

                            "credencial" ->
                                "${usuario.nombre}\n" +
                                        "${usuario.rol} · Zona Sur\n\n" +
                                        "Credencial académica sin validez oficial."

                            "sesiones" ->
                                "Dispositivo actual: sesión local activa. " +
                                        "No hay sesiones remotas."

                            "terminos" ->
                                "MVP académico con cámaras y central " +
                                        "simuladas. Los datos y fotos se guardan " +
                                        "localmente. La verificación en dos pasos " +
                                        "y alertas críticas son preferencias " +
                                        "de demostración."

                            else ->
                                "Inicio: revisa estados.\n" +
                                        "Cámaras: usa los controles simulados.\n" +
                                        "Eventos: filtra y exporta reportes.\n" +
                                        "Alertas: registra su atención.\n" +
                                        "Perfil: guarda tu foto y preferencias."
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        when (tipo) {
                            "password" -> {
                                vm.cambiarPassword(
                                    email = usuario.correo,
                                    actual = actual,
                                    nueva = nueva
                                )

                                actual = ""
                                nueva = ""
                            }

                            "salir" -> onLogout()
                        }

                        dialogo = null
                    }
                ) {
                    Text(
                        when (tipo) {
                            "password" -> "Guardar"
                            "salir" -> "Cerrar sesión"
                            else -> "Entendido"
                        }
                    )
                }
            },
            dismissButton = {
                if (tipo == "password" || tipo == "salir") {
                    TextButton(
                        onClick = { dialogo = null }
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        )
    }
}

