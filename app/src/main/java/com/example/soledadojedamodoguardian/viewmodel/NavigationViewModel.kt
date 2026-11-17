package com.example.soledadojedamodoguardian.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soledadojedamodoguardian.navigation.NavigationEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class NavigationViewModel : ViewModel() {


    private val _events =
        MutableSharedFlow<NavigationEvent>()

    val events = _events.asSharedFlow()


    fun navigateTo(route: String) {
        viewModelScope.launch {
            _events.emit(
                NavigationEvent.NavigateTo(route)
            )
        }
    }


    fun navigateBack() {
        viewModelScope.launch {
            _events.emit(
                NavigationEvent.NavigateBack
            )
        }
    }
}

