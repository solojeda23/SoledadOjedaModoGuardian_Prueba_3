package com.example.soledadojedamodoguardian.data

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "modo_guardian_database"
            )
                .addMigrations(MIGRATION_1_2)
                .build()
                .also { INSTANCE = it }
        }
    }

    // Conserva la tabla de usuarios y agrega las tablas nuevas.
    private val MIGRATION_1_2 = object : Migration(1, 2) {

        override fun migrate(db: SupportSQLiteDatabase) {

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS eventos (
                    id TEXT NOT NULL PRIMARY KEY,
                    titulo TEXT NOT NULL,
                    zona TEXT NOT NULL,
                    camara TEXT NOT NULL,
                    tipo TEXT NOT NULL,
                    prioridad TEXT NOT NULL,
                    estado TEXT NOT NULL,
                    fecha INTEGER NOT NULL,
                    fotoPath TEXT
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS acciones (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    eventoId TEXT NOT NULL,
                    usuario TEXT NOT NULL,
                    accion TEXT NOT NULL,
                    fecha INTEGER NOT NULL,
                    FOREIGN KEY(eventoId)
                        REFERENCES eventos(id)
                        ON DELETE CASCADE
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_acciones_eventoId
                ON acciones(eventoId)
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS perfiles (
                    email TEXT NOT NULL PRIMARY KEY,
                    direccion TEXT NOT NULL,
                    fotoPath TEXT
                )
                """.trimIndent()
            )
        }
    }
}