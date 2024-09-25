package com.example.invoicemanagergrupotorres.ui.screens


import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(navController: NavController) {

    val user = FirebaseAuth.getInstance().currentUser
    Log.d("Auth", "Usuario autenticado: ${user?.uid}")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Bienvenido a InvoiceManager", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("captureInvoice") }) {
            Text("Capturar Factura Física")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { navController.navigate("invoiceList") }) {
            Text("Ver Facturas")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { navController.navigate("monthlySummary") }) {
            Text("Resumen Mensual de IVA")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { navController.navigate("settings") }) {
            Text("Configuración")
        }
    }
}