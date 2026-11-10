package com.example.soledadojedamodoguardian.model

data class LoginUiState(
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val confirmarPassword: String = "",
    val isLoading: Boolean = false,
    val isLoginEnabled: Boolean = false,
    val mensajeError: String = "",
    val usuario: Usuario? = null
)

