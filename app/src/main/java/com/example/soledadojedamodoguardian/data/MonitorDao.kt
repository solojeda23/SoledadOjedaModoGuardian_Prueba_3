package com.example.soledadojedamodoguardian.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MonitorDao {

    @Query("SELECT * FROM eventos ORDER BY fecha DESC")
    fun observar(): Flow<List<MonitorEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(eventos: List<MonitorEntity>)

    @Query("UPDATE eventos SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: String, estado: String)

    @Insert
    suspend fun insertarAccion(accion: AccionEntity)

    @Transaction
    suspend fun atender(
        id: String,
        estado: String,
        usuario: String
    ) {
        actualizarEstado(id, estado)

        insertarAccion(
            AccionEntity(
                eventoId = id,
                usuario = usuario,
                accion = estado
            )
        )
    }

    @Query(
        "SELECT * FROM acciones " +
                "WHERE eventoId = :id ORDER BY fecha DESC"
    )
    fun acciones(id: String): Flow<List<AccionEntity>>

    @Query("SELECT * FROM perfiles WHERE email = :email")
    fun perfil(email: String): Flow<PerfilEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarPerfil(perfil: PerfilEntity)
}
