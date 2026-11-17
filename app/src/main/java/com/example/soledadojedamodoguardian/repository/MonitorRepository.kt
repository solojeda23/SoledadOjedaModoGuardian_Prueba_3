package com.example.soledadojedamodoguardian.repository

import android.content.Context
import com.example.soledadojedamodoguardian.data.AccionEntity
import com.example.soledadojedamodoguardian.data.DatabaseProvider
import com.example.soledadojedamodoguardian.data.MonitorEntity
import com.example.soledadojedamodoguardian.data.PerfilEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.Date
import java.util.UUID

interface GuardianApi {

    @GET("eventos")
    suspend fun eventos(): List<EventoDto>
}

data class EventoDto(
    val id: String,
    val titulo: String,
    val zona: String,
    val camara: String,
    val tipo: String,
    val prioridad: String,
    val minutos: Int
)

class MonitorRepository(context: Context) {

    private val appContext = context.applicationContext

    private val dao = DatabaseProvider
        .getDatabase(appContext)
        .monitorDao()

    val eventos = dao.observar()

    // Servicio simulado: devuelve el JSON local sin acceder a un servidor.
    private val cliente = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val json = appContext.assets
                .open("eventos.json")
                .bufferedReader()
                .use { it.readText() }

            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("API simulada")
                .body(
                    json.toResponseBody(
                        "application/json".toMediaType()
                    )
                )
                .build()
        }
        .build()

    private val api = Retrofit.Builder()
        .baseUrl("https://guardian.test/api/")
        .client(cliente)
        .addConverterFactory(
            GsonConverterFactory.create()
        )
        .build()
        .create(GuardianApi::class.java)

    suspend fun sincronizar(
        fallar: Boolean = false
    ) = withContext(Dispatchers.IO) {

        if (fallar) {
            throw java.io.IOException(
                "Sin conexión de prueba"
            )
        }

        val ahora = System.currentTimeMillis()

        val registros = api.eventos().map { evento ->
            MonitorEntity(
                id = evento.id,
                titulo = evento.titulo,
                zona = evento.zona,
                camara = evento.camara,
                tipo = evento.tipo,
                prioridad = evento.prioridad,
                fecha = Date(
                    ahora - evento.minutos * 60_000L
                )
            )
        }

        dao.insertar(registros)
    }

    suspend fun atender(
        id: String,
        estado: String,
        email: String
    ) {
        dao.atender(id, estado, email)
    }

    fun acciones(id: String) = dao.acciones(id)

    fun perfil(email: String) = dao.perfil(email)

    suspend fun guardarPerfil(perfil: PerfilEntity) {
        dao.guardarPerfil(perfil)
    }

    suspend fun reportar(
        titulo: String,
        zona: String,
        foto: String?,
        email: String
    ) {
        val id = UUID.randomUUID().toString()

        val incidencia = MonitorEntity(
            id = id,
            titulo = titulo,
            zona = zona,
            camara = "MANUAL",
            tipo = "Alerta",
            prioridad = "Alta",
            fotoPath = foto
        )

        dao.insertar(listOf(incidencia))

        dao.insertarAccion(
            AccionEntity(
                eventoId = id,
                usuario = email,
                accion = "Incidencia reportada"
            )
        )
    }
}