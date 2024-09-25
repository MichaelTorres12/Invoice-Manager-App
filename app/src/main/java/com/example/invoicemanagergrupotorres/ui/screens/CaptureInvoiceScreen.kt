package com.example.invoicemanagergrupotorres.ui.screens

import android.Manifest
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.invoicemanagergrupotorres.utils.parseInvoiceText
import com.example.invoicemanagergrupotorres.utils.processImage
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun CaptureInvoiceScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    val previewView = remember { PreviewView(context) }

    // Estado para el permiso de cámara
    var hasCameraPermission by remember { mutableStateOf(false) }

    // Solicitud de permiso de cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    // Solicitar el permiso al iniciar la pantalla
    LaunchedEffect(Unit) {
        val permissionStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionStatus == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            hasCameraPermission = true
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        LaunchedEffect(cameraProviderFuture) {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraX", "Error al configurar la cámara", e)
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                cameraExecutor.shutdown()
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Vista previa de la cámara
            AndroidView(
                factory = { previewView },
                modifier = Modifier.weight(1f)
            )

            // Botón para capturar la imagen
            Button(
                onClick = {
                    // Lógica para capturar la imagen
                    val photoFile = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    imageCapture?.takePicture(
                        outputOptions,
                        cameraExecutor,
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                // Mover la imagen al directorio de archivos de la aplicación
                                val fileName = "${System.currentTimeMillis()}.jpg"
                                val destinationFile = File(context.filesDir, fileName)
                                photoFile.copyTo(destinationFile, overwrite = true)
                                // Eliminar el archivo original en caché si lo deseas
                                photoFile.delete()
                                val savedUri = Uri.fromFile(destinationFile)

                                // Procesar la imagen capturada
                                processImage(context, savedUri, onResult = { text ->
                                    // Manejar el texto extraído
                                    Log.d("MLKit", "Texto reconocido: $text")
                                    // Parsear los datos de la factura
                                    val invoiceData = parseInvoiceText(text)
                                    // Navegar a la pantalla de edición, pasando los datos y la URI de la imagen
                                    navController.currentBackStackEntry?.savedStateHandle?.set("invoiceData", invoiceData)
                                    navController.currentBackStackEntry?.savedStateHandle?.set("imageUri", savedUri.toString())
                                    navController.navigate("editInvoice")
                                }, onError = { e ->
                                    Log.e("MLKit", "Error al procesar la imagen", e)
                                    Toast.makeText(context, "Error al procesar la imagen", Toast.LENGTH_LONG).show()
                                })
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("CameraX", "Error al capturar imagen: ${exception.message}", exception)
                                Toast.makeText(context, "Error al capturar la imagen: ${exception.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Capturar Factura")
            }
        }
    } else {
        // Mostrar un mensaje indicando que se requiere el permiso
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Se requiere permiso de cámara para capturar facturas.")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }) {
                Text("Conceder Permiso")
            }
        }
    }
}

