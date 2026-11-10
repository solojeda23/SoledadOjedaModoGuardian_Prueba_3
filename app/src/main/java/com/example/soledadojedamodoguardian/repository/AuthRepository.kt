package com.example.soledadojedamodoguardian.repository

import android.content.Context
import com.example.soledadojedamodoguardian.data.DatabaseProvider
import com.example.soledadojedamodoguardian.data.PasswordHasher
import com.example.soledadojedamodoguardian.data.UsuarioEntity
import com.example.soledadojedamodoguardian.model.Rol
import com.example.soledadojedamodoguardian.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(context: Context) {

    private val usuarioDao =
        DatabaseProvider.getDatabase(context).usuarioDao()

    // Usuarios de prueba de la guía
    private val cuentasPrueba = mapOf(
        "admin@guardian.test" to Cuenta(
            "Administrador", "123456", Rol.ADMIN
        ),
        "supervisor@guardian.test" to Cuenta(
            "Supervisor", "123456", Rol.SUPERVISOR
        ),
        "operador@guardian.test" to Cuenta(
            "Operador", "123456", Rol.OPERADOR
        )
    )

    // Iniciar sesión
    suspend fun login(
        email: String,
        password: String
    ): Usuario? = withContext(Dispatchers.IO) {

        val correo = email.trim().lowercase()

        // Mantener usuarios de prueba
        val cuentaPrueba = cuentasPrueba[correo]

        if (cuentaPrueba != null) {
            return@withContext if (
                cuentaPrueba.password == password
            ) {
                Usuario(
                    nombre = cuentaPrueba.nombre,
                    correo = correo,
                    rol = cuentaPrueba.rol
                )
            } else null
        }

        // Buscar usuario en Room
        val usuario = usuarioDao.buscarPorEmail(correo)
            ?: return@withContext null

        val passwordCorrecta =
            PasswordHasher.verificarPassword(
                password,
                usuario.passwordSalt,
                usuario.passwordHash
            )

        if (!passwordCorrecta) {
            return@withContext null
        }

        Usuario(
            nombre = usuario.nombre,
            correo = usuario.email,
            rol = Rol.valueOf(usuario.rol)
        )
    }

    // Registrar usuarios en Room
    suspend fun registrar(
        nombre: String,
        email: String,
        password: String
    ): Boolean = withContext(Dispatchers.IO) {

        val correo = email.trim().lowercase()

        if (cuentasPrueba.containsKey(correo)) {
            return@withContext false
        }

        if (usuarioDao.buscarPorEmail(correo) != null) {
            return@withContext false
        }

        val salt = PasswordHasher.generarSalt()
        val hash = PasswordHasher.generarHash(
            password,
            salt
        )

        val nuevoUsuario = UsuarioEntity(
            nombre = nombre.trim(),
            email = correo,
            passwordHash = hash,
            passwordSalt = salt,
            rol = Rol.OPERADOR.name
        )

        try {
            usuarioDao.insertarUsuario(nuevoUsuario)
            true
        } catch (e: android.database.sqlite.SQLiteConstraintException) {
            false
        }
    }

    private data class Cuenta(
        val nombre: String,
        val password: String,
        val rol: Rol
    )
}