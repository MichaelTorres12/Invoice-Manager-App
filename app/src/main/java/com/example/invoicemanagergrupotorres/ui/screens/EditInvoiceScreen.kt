package com.example.invoicemanagergrupotorres.ui.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.invoicemanagergrupotorres.data.models.InvoiceData
import com.example.invoicemanagergrupotorres.data.saveInvoiceData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EditInvoiceScreen(navController: NavController, invoiceData: InvoiceData, imageUri: Uri) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var amount by remember { mutableStateOf(invoiceData.amount.toString()) }
    var date by remember { mutableStateOf(dateFormat.format(invoiceData.date)) }
    var issuer by remember { mutableStateOf(invoiceData.issuer) }
    var reason by remember { mutableStateOf(invoiceData.reason) }
    var iva by remember { mutableStateOf(invoiceData.iva.toString()) }
    var invoiceType by remember { mutableStateOf(invoiceData.invoiceType) }

    // Variable de estado para manejar el estado de carga
    var isLoading by remember { mutableStateOf(false) }

    // Contenido principal
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Editar Datos de la Factura", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            // Campos de texto para editar los datos
            OutlinedTextField(
                value = issuer,
                onValueChange = { issuer = it },
                label = { Text("Emisor") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Fecha (dd/MM/yyyy)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Monto Total") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            OutlinedTextField(
                value = iva,
                onValueChange = { iva = it },
                label = { Text("IVA") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Concepto") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            OutlinedTextField(
                value = invoiceType,
                onValueChange = { invoiceType = it },
                label = { Text("Tipo de Factura") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    Log.d("EditInvoiceScreen", "Botón 'Guardar' presionado")
                    isLoading = true  // Iniciar carga
                    // Parsear la fecha ingresada
                    val parsedDate = try {
                        dateFormat.parse(date)
                    } catch (e: Exception) {
                        null
                    } ?: Date()

                    // Subir la imagen a Firebase Storage
                    scope.launch {
                        uploadImageToFirebaseStorage(
                            context = context,
                            imageUri = imageUri,
                            onSuccess = { imageUrl ->
                                val updatedInvoiceData = InvoiceData(
                                    amount = amount.toDoubleOrNull() ?: 0.0,
                                    date = parsedDate,
                                    issuer = issuer,
                                    reason = reason,
                                    iva = iva.toDoubleOrNull() ?: 0.0,
                                    invoiceType = invoiceType,
                                    pdfUrl = imageUrl  // Incluir la URL de la imagen
                                )

                                Log.d("EditInvoiceScreen", "Datos a guardar: $updatedInvoiceData")

                                // Guardar en Firestore
                                saveInvoiceData(updatedInvoiceData, onSuccess = {
                                    Log.d("EditInvoiceScreen", "Datos guardados exitosamente en Firestore")
                                    Toast.makeText(context, "Factura guardada exitosamente", Toast.LENGTH_LONG).show()
                                    isLoading = false  // Detener carga
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }, onFailure = { e ->
                                    // Manejar el error
                                    Log.e("EditInvoiceScreen", "Error al guardar en Firestore", e)
                                    isLoading = false  // Detener carga
                                    Toast.makeText(context, "Error al guardar la factura: ${e.message}", Toast.LENGTH_LONG).show()
                                })
                            },
                            onFailure = { e ->
                                Log.e("EditInvoiceScreen", "Error al subir la imagen", e)
                                isLoading = false  // Detener carga
                                Toast.makeText(context, "Error al subir la imagen: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading  // Deshabilitar botón durante la carga
            ) {
                Text("Guardar")
            }
        }

        // Indicador de carga
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

// Función para subir la imagen a Firebase Storage
private suspend fun uploadImageToFirebaseStorage(
    context: Context,
    imageUri: Uri,
    onSuccess: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
    try {
        val storageRef = FirebaseStorage.getInstance().reference
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"
        val imageRef = storageRef.child("users/$userId/invoices/${imageUri.lastPathSegment}")

        imageRef.putFile(imageUri).await()
        val downloadUrl = imageRef.downloadUrl.await()
        onSuccess(downloadUrl.toString())
    } catch (e: Exception) {
        onFailure(e)
    }
}

