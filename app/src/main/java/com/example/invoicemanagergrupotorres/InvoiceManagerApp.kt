package com.example.invoicemanagergrupotorres

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.invoicemanagergrupotorres.data.models.InvoiceData
import com.example.invoicemanagergrupotorres.ui.screens.CaptureInvoiceScreen
import com.example.invoicemanagergrupotorres.ui.screens.EditInvoiceScreen
import com.example.invoicemanagergrupotorres.ui.screens.HomeScreen
import com.example.invoicemanagergrupotorres.ui.screens.SignInScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun InvoiceManagerApp() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = if (currentUser != null) "home" else "signIn"
    ) {
        composable("signIn") { SignInScreen { navController.navigate("home") } }
        composable("home") { HomeScreen(navController) }
        composable("captureInvoice") { CaptureInvoiceScreen(navController) }

        composable("editInvoice") { backStackEntry ->
            val previousBackStackEntry = navController.previousBackStackEntry
            val invoiceData = previousBackStackEntry?.savedStateHandle?.get<InvoiceData>("invoiceData")
            val imageUriString = previousBackStackEntry?.savedStateHandle?.get<String>("imageUri")
            val imageUri = imageUriString?.let { Uri.parse(it) }

            if (invoiceData != null && imageUri != null) {
                EditInvoiceScreen(navController, invoiceData, imageUri)
            } else {
                Log.e("EditInvoiceScreen", "Debo de arreglar este error ya que los datos e imagen si se guardan en la DB")
            }
        }
    }
}
