package com.example.soledadojedamodoguardian.model


enum class Rol {
    ADMIN,
    SUPERVISOR,
    OPERADOR
}


data class Usuario(
    val nombre: String,
    val correo: String,
    val rol: Rol
)
