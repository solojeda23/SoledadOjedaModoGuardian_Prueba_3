package com.example.soledadojedamodoguardian.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.soledadojedamodoguardian.model.*
import com.example.soledadojedamodoguardian.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _errores = MutableStateFlow(LoginErrores())
    val errores = _errores.asStateFlow()

    fun onNombreChange(valor: String) {
        _uiState.update {
            it.copy(nombre = valor)
        }
    }

    fun onEmailChange(valor: String) {
        _uiState.update {
            it.copy(
                email = valor,
                isLoginEnabled = valor.isNotBlank() &&
                        it.password.isNotBlank(),
                mensajeError = ""
            )
        }
    }

    fun onPasswordChange(valor: String) {
        _uiState.update {
            it.copy(
                password = valor,
                isLoginEnabled = it.email.isNotBlank() &&
                        valor.isNotBlank(),
                mensajeError = ""
            )
        }
    }

    fun onConfirmarPasswordChange(valor: String) {
        _uiState.update {
            it.copy(confirmarPassword = valor)
        }
    }

    fun validarFormulario(
        registro: Boolean = false
    ): Boolean {

        val estado = _uiState.value

        val nombreError = if (
            registro && estado.nombre.trim().length < 3
        ) {
            "El nombre debe tener al menos 3 caracteres"
        } else null

        val emailError = if (
            !Patterns.EMAIL_ADDRESS.matcher(
                estado.email.trim()
            ).matches()
        ) {
            "Ingresa un correo válido"
        } else null

        val passwordError = if (
            estado.password.length < 6
        ) {
            "La contraseña debe tener mínimo 6 caracteres"
        } else null

        val confirmarError = if (
            registro &&
            estado.password != estado.confirmarPassword
        ) {
            "Las contraseñas no coinciden"
        } else null

        _errores.value = LoginErrores(
            nombreError = nombreError,
            emailError = emailError,
            passwordError = passwordError,
            confirmarPasswordError = confirmarError
        )

        return nombreError == null &&
                emailError == null &&
                passwordError == null &&
                confirmarError == null
    }

    // Inicio de sesión utilizando Room
    fun iniciarSesion(
        onResultado: (Boolean) -> Unit
    ) {
        if (!validarFormulario()) {
            onResultado(false)
            return
        }

        if (_uiState.value.isLoading) return

        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            try {
                val estado = _uiState.value

                val usuario = repository.login(
                    estado.email,
                    estado.password
                )

                _uiState.update {
                    it.copy(
                        usuario = usuario,
                        isLoading = false,
                        mensajeError = if (usuario == null)
                            "Correo o contraseña incorrectos"
                        else ""
                    )
                }

                onResultado(usuario != null)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        mensajeError = "Ocurrió un error inesperado"
                    )
                }
                onResultado(false)
            }
        }
    }

    // Registro de usuarios utilizando Room
    fun registrar(
        onResultado: (Boolean) -> Unit
    ) {
        if (!validarFormulario(registro = true)) {
            onResultado(false)
            return
        }

        if (_uiState.value.isLoading) return

        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            try {
                val estado = _uiState.value

                val registrado = repository.registrar(
                    estado.nombre,
                    estado.email,
                    estado.password
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        mensajeError = if (!registrado)
                            "Este correo ya está registrado"
                        else "",
                        confirmarPassword = ""
                    )
                }

                onResultado(registrado)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        mensajeError = "No se pudo registrar el usuario"
                    )
                }
                onResultado(false)
            }
        }
    }

    fun cerrarSesion() {
        _uiState.value = LoginUiState()
        _errores.value = LoginErrores()
    }
}