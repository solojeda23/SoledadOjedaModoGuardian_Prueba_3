package com.example.soledadojedamodoguardian.navigation

// Eventos para controlar la navegación
sealed class NavigationEvent {

    // Permite navegar a una pantalla específica
    data class NavigateTo(
        val route: String
    ) : NavigationEvent()

    // Permite regresar a la pantalla anterior
    data object NavigateBack : NavigationEvent()
}
