package com.example.soledadojedamodoguardian.model

// Clase que almacena los errores del formulario
data class LoginErrores(

    // Error en el nombre de usuario
    val nombreError: String? = null,

    // Error en el correo electrónico
    val emailError: String? = null,

    // Error en la contraseña
    val passwordError: String? = null,

    // Error si las contraseñas no coinciden
    val confirmarPasswordError: String? = null

)