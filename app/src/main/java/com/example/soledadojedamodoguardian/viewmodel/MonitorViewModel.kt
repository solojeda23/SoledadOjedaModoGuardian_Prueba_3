package com.example.soledadojedamodoguardian.viewmodel

import android.app.Application
import android.net.Uri
import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.soledadojedamodoguardian.data.DatabaseProvider
import com.example.soledadojedamodoguardian.data.PasswordHasher
import com.example.soledadojedamodoguardian.data.PerfilEntity
import com.example.soledadojedamodoguardian.data.PreferencesManager
import com.example.soledadojedamodoguardian.data.MonitorEntity
import com.example.soledadojedamodoguardian.model.Rol
import com.example.soledadojedamodoguardian.repository.AuthRepository
import com.example.soledadojedamodoguardian.repository.MonitorRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class MonitorState(
    val cargando: Boolean = false,
    val error: String = "",
    val mensaje: String = "",
    val sinConexion: Boolean = false
)

class MonitorViewModel(
    app: Application
) : AndroidViewModel(app) {

    private val repo = MonitorRepository(app)
    private val prefs = PreferencesManager(app)

    val eventos = repo.eventos.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val preferencias = prefs.datos.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyPreferences()
    )

    val usuarios = DatabaseProvider
        .getDatabase(app)
        .usuarioDao()
        .observarUsuarios()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _estado = MutableStateFlow(MonitorState())
    val estado = _estado.asStateFlow()

    init {
        sincronizar()
    }

    private fun ejecutar(
        block: suspend () -> Unit
    ) = viewModelScope.launch {
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _estado.update {
                it.copy(
                    error = e.message
                        ?: "No se pudo completar la acción",
                    cargando = false
                )
            }
        }
    }

    fun sincronizar() = ejecutar {
        _estado.update {
            it.copy(
                cargando = true,
                error = ""
            )
        }

        try {
            delay(500)

            repo.sincronizar(
                fallar = _estado.value.sinConexion
            )

            mensaje("Datos de prueba sincronizados")
        } finally {
            _estado.update {
                it.copy(cargando = false)
            }
        }
    }

    fun conexion(valor: Boolean) {
        _estado.update {
            it.copy(sinConexion = valor)
        }
    }

    fun mensaje(texto: String) {
        _estado.update {
            it.copy(mensaje = texto)
        }
    }

    fun limpiarMensaje() {
        _estado.update {
            it.copy(mensaje = "")
        }
    }

    fun atender(
        evento: MonitorEntity,
        estado: String,
        email: String,
        rol: Rol
    ) = ejecutar {

        if (rol == Rol.SUPERVISOR && estado == "Falsa alarma") {
            mensaje(
                "El operador o administrador debe descartar la alerta"
            )
            return@ejecutar
        }

        if (evento.estado != "Pendiente") {
            mensaje("Este evento ya fue atendido")
            return@ejecutar
        }

        repo.atender(evento.id, estado, email)
        mensaje("Estado actualizado y acción guardada")
    }

    fun acciones(id: String) = repo.acciones(id)

    fun perfil(email: String) = repo.perfil(email)

    fun guardarPerfil(
        email: String,
        direccion: String,
        foto: String?
    ) = ejecutar {

        if (direccion.isBlank()) {
            mensaje("Ingresa una dirección")
            return@ejecutar
        }

        repo.guardarPerfil(
            PerfilEntity(
                email = email,
                direccion = direccion.trim(),
                fotoPath = foto
            )
        )

        mensaje("Perfil guardado")
    }

    fun preferencia(
        clave: String,
        valor: Boolean
    ) = ejecutar {
        prefs.guardar(clave, valor)
        mensaje("Preferencia guardada")
    }

    fun reportar(
        titulo: String,
        zona: String,
        foto: String?,
        email: String,
        onGuardado: () -> Unit
    ) = ejecutar {

        require(
            titulo.trim().length >= 5 &&
                    zona.isNotBlank() &&
                    foto != null
        ) {
            "Completa descripción, zona y foto"
        }

        repo.reportar(
            titulo = titulo.trim(),
            zona = zona.trim(),
            foto = foto,
            email = email
        )

        mensaje("Incidencia guardada en el historial")
        onGuardado()
    }

    suspend fun copiarImagen(uri: Uri): String =
        withContext(Dispatchers.IO) {

            val contexto = getApplication<Application>()

            val destino = File(
                contexto.filesDir,
                "foto_${System.nanoTime()}.jpg"
            )

            try {
                contexto.contentResolver
                    .openInputStream(uri)
                    .use { origen ->

                        requireNotNull(origen) {
                            "No se pudo abrir la imagen"
                        }

                        destino.outputStream().use { salida ->
                            origen.copyTo(salida)
                        }
                    }

                destino.absolutePath
            } catch (e: Exception) {
                destino.delete()
                throw e
            }
        }

    fun cambiarRol(
        id: Int,
        rol: Rol,
        solicitante: Rol
    ) = ejecutar {

        require(solicitante == Rol.ADMIN) {
            "Solo el administrador puede cambiar roles"
        }

        DatabaseProvider
            .getDatabase(getApplication<Application>())
            .usuarioDao()
            .cambiarRol(id, rol.name)

        mensaje("Rol actualizado")
    }

    fun cambiarPassword(
        email: String,
        actual: String,
        nueva: String
    ) = ejecutar {

        require(nueva.length >= 6) {
            "La nueva contraseña debe tener al menos 6 caracteres"
        }

        val usuario = AuthRepository(
            getApplication<Application>()
        ).login(email, actual)

        require(usuario != null) {
            "Contraseña actual incorrecta"
        }

        require(!email.endsWith("@guardian.test")) {
            "Las cuentas de demostración conservan su contraseña de prueba"
        }

        withContext(Dispatchers.IO) {
            val salt = PasswordHasher.generarSalt()

            val hash = PasswordHasher.generarHash(
                nueva,
                salt
            )

            DatabaseProvider
                .getDatabase(getApplication<Application>())
                .usuarioDao()
                .cambiarPassword(
                    email = email,
                    hash = hash,
                    salt = salt
                )
        }

        mensaje("Contraseña actualizada")
    }
}