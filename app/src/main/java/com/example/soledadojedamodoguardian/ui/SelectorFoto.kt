package com.example.soledadojedamodoguardian.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.soledadojedamodoguardian.viewmodel.MonitorViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ImagenInteligente(
    foto: String?,
    size: Int = 96
) {
    if (foto != null) {
        AsyncImage(
            model = File(foto),
            contentDescription = "Foto seleccionada",
            modifier = androidx.compose.ui.Modifier
                .size(size.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(
                alpha = 0.2f
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Perfil sin foto",
                modifier = androidx.compose.ui.Modifier
                    .size(size.dp)
                    .padding(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SelectorFoto(
    vm: MonitorViewModel,
    foto: String?,
    onFoto: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var pendiente by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    fun copiar(uri: Uri, temporal: File? = null) {
        scope.launch {
            try {
                val ruta = vm.copiarImagen(uri)
                onFoto(ruta)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                vm.mensaje(
                    "No se pudo guardar la foto. Intenta otra vez"
                )
            } finally {
                temporal?.delete()
            }
        }
    }

    val camara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { resultado ->
        val ruta = pendiente
        pendiente = null

        if (ruta != null) {
            val archivo = File(ruta)

            if (resultado) {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.files",
                    archivo
                )

                copiar(uri, archivo)
            } else {
                archivo.delete()
            }
        }
    }

    val galeria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            copiar(uri)
        }
    }

    Column(
        modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ImagenInteligente(foto = foto)

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    try {
                        val archivo = File.createTempFile(
                            "captura_",
                            ".jpg",
                            context.cacheDir
                        )

                        pendiente = archivo.absolutePath

                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.files",
                            archivo
                        )

                        camara.launch(uri)
                    } catch (e: Exception) {
                        pendiente?.let { File(it).delete() }
                        pendiente = null

                        vm.mensaje(
                            "No se pudo abrir la cámara"
                        )
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = null
                )

                Text(" Cámara")
            }

            OutlinedButton(
                onClick = {
                    try {
                        galeria.launch("image/*")
                    } catch (e: Exception) {
                        vm.mensaje(
                            "No se pudo abrir la galería"
                        )
                    }
                }
            ) {
                Text("Galería")
            }
        }
    }
}

