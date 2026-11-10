package com.example.soledadojedamodoguardian.navigation

// Define las pantallas de la aplicación
sealed class Screen(val route: String) {

    // Pantalla para iniciar sesión
    data object Login : Screen("login")

    // Pantalla para registrar usuarios
    data object Registro : Screen("registro")

    // Pantalla del administrador
    data object HomeAdmin : Screen("home_admin")

    // Pantalla del supervisor
    data object HomeSupervisor : Screen("home_supervisor")

    // Pantalla del operador
    data object HomeOperador : Screen("home_operador")
}