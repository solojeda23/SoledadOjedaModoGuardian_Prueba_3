package com.example.soledadojedamodoguardian.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UsuarioEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
}