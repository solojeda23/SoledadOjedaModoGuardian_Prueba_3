package com.example.soledadojedamodoguardian.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.Date

@Entity(tableName = "eventos")
data class MonitorEntity(
    @PrimaryKey
    val id: String,
    val titulo: String,
    val zona: String,
    val camara: String,
    val tipo: String,
    val prioridad: String,
    val estado: String = "Pendiente",
    val fecha: Date = Date(),
    val fotoPath: String? = null
)

@Entity(
    tableName = "acciones",
    foreignKeys = [
        ForeignKey(
            entity = MonitorEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["eventoId"])]
)
data class AccionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val eventoId: String,
    val usuario: String,
    val accion: String,
    val fecha: Date = Date()
)

@Entity(tableName = "perfiles")
data class PerfilEntity(
    @PrimaryKey
    val email: String,
    val direccion: String = "",
    val fotoPath: String? = null
)

class Converters {

    // Guarda las fechas como milisegundos en SQLite.
    @TypeConverter
    fun fechaALong(fecha: Date?): Long? {
        return fecha?.time
    }

    // Recupera los milisegundos como una fecha.
    @TypeConverter
    fun longAFecha(valor: Long?): Date? {
        return valor?.let { Date(it) }
    }
}

