package com.example.invoicemanagergrupotorres.data

import android.util.Log
import com.example.invoicemanagergrupotorres.data.models.InvoiceData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

fun saveInvoiceData(
    invoiceData: InvoiceData,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    if (userId != null) {
        Log.d("FirestoreRepository", "Guardando datos para el usuario: $userId")
        Log.d("FirestoreRepository", "InvoiceData: $invoiceData")

        db.collection("users").document(userId).collection("invoices")
            .add(invoiceData)
            .addOnSuccessListener {
                Log.d("FirestoreRepository", "Datos guardados exitosamente en Firestore")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreRepository", "Error al guardar en Firestore", e)
                onFailure(e)
            }
    } else {
        Log.e("FirestoreRepository", "Usuario no autenticado")
        onFailure(Exception("Usuario no autenticado"))
    }
}

suspend fun getInvoicesForCurrentUser(): List<InvoiceData> {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    if (userId != null) {
        val snapshot = db.collection("users")
            .document(userId)
            .collection("invoices")
            .get()
            .await()

        return snapshot.documents.mapNotNull { document ->
            document.toObject(InvoiceData::class.java)
        }
    } else {
        throw Exception("Usuario no autenticado")
    }
}

