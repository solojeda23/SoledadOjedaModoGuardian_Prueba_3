package com.example.soledadojedamodoguardian.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertarUsuario(usuario: UsuarioEntity)

    @Query(
        "SELECT * FROM usuarios " +
                "WHERE email = :email LIMIT 1"
    )
    suspend fun buscarPorEmail(email: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios")
    suspend fun obtenerUsuarios(): List<UsuarioEntity>

    @Query("SELECT * FROM usuarios ORDER BY nombre")
    fun observarUsuarios(): Flow<List<UsuarioEntity>>

    @Query(
        "UPDATE usuarios SET rol = :rol " +
                "WHERE id = :id"
    )
    suspend fun cambiarRol(id: Int, rol: String)

    @Query(
        "UPDATE usuarios " +
                "SET passwordHash = :hash, passwordSalt = :salt " +
                "WHERE email = :email"
    )
    suspend fun cambiarPassword(
        email: String,
        hash: String,
        salt: String
    )
}