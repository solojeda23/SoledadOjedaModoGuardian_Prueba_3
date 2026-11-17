package com.example.soledadojedamodoguardian.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        UsuarioEntity::class,
        MonitorEntity::class,
        AccionEntity::class,
        PerfilEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    abstract fun monitorDao(): MonitorDao
}