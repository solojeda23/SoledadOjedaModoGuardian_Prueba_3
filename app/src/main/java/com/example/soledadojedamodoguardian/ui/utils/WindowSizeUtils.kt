package com.example.soledadojedamodoguardian.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

// Categorías de tamaño de pantalla
enum class WindowSize {
    COMPACT,
    MEDIUM,
    EXPANDED
}

// Calcula el tamaño de la ventana
@Composable
fun calcularWindowSize(): WindowSize {

    val configuration = LocalConfiguration.current
    val anchoPantalla = configuration.screenWidthDp

    return when {
        anchoPantalla < 600 -> WindowSize.COMPACT
        anchoPantalla < 840 -> WindowSize.MEDIUM
        else -> WindowSize.EXPANDED
    }
}