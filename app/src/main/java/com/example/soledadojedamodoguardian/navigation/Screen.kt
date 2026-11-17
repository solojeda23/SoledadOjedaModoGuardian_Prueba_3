package com.example.soledadojedamodoguardian.navigation

// Define las pantallas de la aplicación
sealed class Screen(val route: String) {


    data object Login : Screen("login")


    data object Registro : Screen("registro")


    data object HomeAdmin : Screen("home_admin")


    data object HomeSupervisor : Screen("home_supervisor")

   
    data object HomeOperador : Screen("home_operador")
}