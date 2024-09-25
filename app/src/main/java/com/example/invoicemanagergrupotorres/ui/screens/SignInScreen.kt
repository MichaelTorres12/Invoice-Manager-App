package com.example.invoicemanagergrupotorres.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.invoicemanagergrupotorres.MainActivity

@Composable
fun SignInScreen(onSignInSuccess: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? MainActivity

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Bienvenido a InvoiceManager", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { activity?.signInWithGoogle(onSignInSuccess) }) {
            Text(text = "Iniciar sesión con Google")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Aquí podriamos agregar componentes para inicio de sesión con email/contraseña
    }
}
