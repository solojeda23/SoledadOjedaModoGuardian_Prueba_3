package com.example.soledadojedamodoguardian.ui.utils

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

enum class WindowSize {
    COMPACT,
    MEDIUM,
    EXPANDED
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun calcularWindowSize(): WindowSize {

    val configuration = LocalConfiguration.current

    val sizeClass = WindowSizeClass.calculateFromSize(
        DpSize(
            width = configuration.screenWidthDp.dp,
            height = configuration.screenHeightDp.dp
        )
    )

    return when (sizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> WindowSize.COMPACT
        WindowWidthSizeClass.Medium -> WindowSize.MEDIUM
        else -> WindowSize.EXPANDED
    }
}