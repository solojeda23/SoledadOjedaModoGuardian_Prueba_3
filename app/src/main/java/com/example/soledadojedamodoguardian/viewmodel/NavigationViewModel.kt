package com.example.soledadojedamodoguardian.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soledadojedamodoguardian.navigation.NavigationEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// Controla los eventos de navegación de la aplicación
class NavigationViewModel : ViewModel() {

    // Flujo de eventos para cambiar de pantalla
    private val _events =
        MutableSharedFlow<NavigationEvent>()

    val events = _events.asSharedFlow()

    // Función para ir a otra pantalla
    fun navigateTo(route: String) {
        viewModelScope.launch {
            _events.emit(
                NavigationEvent.NavigateTo(route)
            )
        }
    }

    // Función para volver a la pantalla anterior
    fun navigateBack() {
        viewModelScope.launch {
            _events.emit(
                NavigationEvent.NavigateBack
            )
        }
    }
}

