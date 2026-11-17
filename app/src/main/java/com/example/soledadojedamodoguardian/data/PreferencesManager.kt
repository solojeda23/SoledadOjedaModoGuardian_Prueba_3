package com.example.soledadojedamodoguardian.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import java.io.IOException

private val Context.guardianDataStore by preferencesDataStore(
    name = "guardian_preferencias"
)

class PreferencesManager(context: Context) {

    private val store = context.applicationContext.guardianDataStore

    val datos = store.data.catch { error ->
        if (error is IOException) {
            emit(emptyPreferences())
        } else {
            throw error
        }
    }

    suspend fun guardar(clave: String, valor: Boolean) {
        store.edit { preferencias ->
            preferencias[booleanPreferencesKey(clave)] = valor
        }
    }

    suspend fun recordar(correo: String) {
        store.edit { preferencias ->
            preferencias[stringPreferencesKey("correo")] = correo
        }
    }
}
