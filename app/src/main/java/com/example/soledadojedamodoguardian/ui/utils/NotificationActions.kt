package com.example.soledadojedamodoguardian.ui.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.soledadojedamodoguardian.MainActivity

fun notificarPrueba(context: Context): Boolean {

    if (Build.VERSION.SDK_INT >= 33) {
        val permiso = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        )

        if (permiso != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }

    val notificaciones = NotificationManagerCompat.from(context)

    if (!notificaciones.areNotificationsEnabled()) {
        return false
    }

    val manager = context.getSystemService(
        NotificationManager::class.java
    )

    val canal = NotificationChannel(
        "guardian_prueba",
        "Alertas de prueba",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Notificaciones académicas de Modo Guardián"
    }

    manager.createNotificationChannel(canal)

    val intent = Intent(
        context,
        MainActivity::class.java
    ).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
    }

    val abrirApp = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE
    )

    val notificacion = NotificationCompat.Builder(
        context,
        "guardian_prueba"
    )
        .setSmallIcon(android.R.drawable.ic_dialog_alert)
        .setContentTitle("Modo Guardián · Prueba")
        .setContentText(
            "Movimiento simulado en Perímetro Norte."
        )
        .setStyle(
            NotificationCompat.BigTextStyle().bigText(
                "Movimiento simulado en Perímetro Norte. " +
                        "Abre la aplicación y revisa Alertas."
            )
        )
        .setContentIntent(abrirApp)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    return try {
        notificaciones.notify(
            100,
            notificacion
        )

        true
    } catch (e: SecurityException) {
        false
    }
}

