package com.example.invoicemanagergrupotorres.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.invoicemanagergrupotorres.data.getInvoicesForCurrentUser
import com.example.invoicemanagergrupotorres.data.models.InvoiceData
import com.example.invoicemanagergrupotorres.viewmodel.SharedViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewInvoicesScreen(navController: NavController, sharedViewModel: SharedViewModel) {
    val invoices = remember { mutableStateListOf<InvoiceData>() }
    val isLoading = remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val fetchedInvoices = getInvoicesForCurrentUser()
                invoices.addAll(fetchedInvoices)
                isLoading.value = false
            } catch (e: Exception) {
                Log.e("ViewInvoicesScreen", "Error al obtener las facturas", e)
                isLoading.value = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Facturas") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (invoices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tienes facturas guardadas.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    items(invoices) { invoice ->
                        InvoiceCard(invoice = invoice, navController = navController, sharedViewModel = sharedViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun InvoiceCard(invoice: InvoiceData, navController: NavController, sharedViewModel: SharedViewModel) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                sharedViewModel.selectedInvoice = invoice
                navController.navigate("invoiceDetail")
            },
        elevation = CardDefaults.cardElevation()
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            if (invoice.pdfUrl != null) {
                AsyncImage(
                    model = invoice.pdfUrl,
                    contentDescription = "Imagen de la factura",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 16.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder en caso de que no haya imagen
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sin imagen")
                }
            }
            Column {
                Text("Emisor: ${invoice.issuer}", style = MaterialTheme.typography.titleMedium)
                Text("Fecha: ${dateFormat.format(invoice.date)}", style = MaterialTheme.typography.bodyMedium)
                Text("Monto: $${invoice.amount}", style = MaterialTheme.typography.bodyMedium)
                Text("Concepto: ${invoice.reason}", style = MaterialTheme.typography.bodyMedium)
                // Agregar los demas campos cuando los implementemos en la toma de foto
            }
        }
    }
}
