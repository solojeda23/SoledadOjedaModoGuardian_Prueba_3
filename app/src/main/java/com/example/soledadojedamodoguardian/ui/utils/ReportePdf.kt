package com.example.soledadojedamodoguardian.ui.utils

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.soledadojedamodoguardian.data.MonitorEntity
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun escribirReporte(
    salida: OutputStream,
    eventos: List<MonitorEntity>,
    usuario: String
) {
    val documento = PdfDocument()

    val pintura = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 11f
    }

    val formato = SimpleDateFormat(
        "dd/MM/yyyy HH:mm",
        Locale("es", "CL")
    )

    var pagina: PdfDocument.Page? = null
    var numeroPagina = 0
    var posicionY = 45f

    fun nuevaPagina() {
        pagina?.let {
            documento.finishPage(it)
        }

        numeroPagina++

        val informacion = PdfDocument.PageInfo.Builder(
            595,
            842,
            numeroPagina
        ).create()

        pagina = documento.startPage(informacion)
        posicionY = 45f
    }

    fun dibujarLinea(texto: String) {
        if (pagina == null || posicionY > 790f) {
            nuevaPagina()
        }

        pagina?.canvas?.drawText(
            texto,
            35f,
            posicionY,
            pintura
        )

        posicionY += 19f
    }

    fun escribirTexto(texto: String) {
        if (texto.isBlank()) {
            dibujarLinea("")
            return
        }

        texto.lines().forEach { parrafo ->
            var pendiente = parrafo

            if (pendiente.isEmpty()) {
                dibujarLinea("")
            }

            while (pendiente.isNotEmpty()) {
                val cantidad = pintura.breakText(
                    pendiente,
                    true,
                    525f,
                    null
                ).coerceAtLeast(1)

                dibujarLinea(
                    pendiente.take(cantidad)
                )

                pendiente = pendiente
                    .drop(cantidad)
                    .trimStart()
            }
        }
    }

    try {
        escribirTexto("MODO GUARDIÁN")
        escribirTexto("Reporte académico · Datos simulados")
        escribirTexto("Usuario: $usuario")
        escribirTexto("Generado: ${formato.format(Date())}")
        escribirTexto("Registros incluidos: ${eventos.size}")
        escribirTexto("")

        eventos.forEachIndexed { indice, evento ->
            escribirTexto(
                "${indice + 1}. ${evento.titulo}"
            )

            escribirTexto(
                "Fecha: ${formato.format(evento.fecha)}"
            )

            escribirTexto(
                "Cámara: ${evento.camara}"
            )

            escribirTexto(
                "Zona: ${evento.zona}"
            )

            escribirTexto(
                "Tipo: ${evento.tipo} | " +
                        "Prioridad: ${evento.prioridad}"
            )

            escribirTexto(
                "Estado: ${evento.estado}"
            )

            escribirTexto("")
        }

        pagina?.let {
            documento.finishPage(it)
        }

        documento.writeTo(salida)
    } finally {
        documento.close()
    }
}

