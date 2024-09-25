package com.example.invoicemanagergrupotorres.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

fun processImage(context: Context, imageUri: Uri, onResult: (String) -> Unit, onError: (Exception) -> Unit) {
    try {
        val image = InputImage.fromFilePath(context, imageUri)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val resultText = visionText.text
                Log.d("processImage", "Texto reconocido: $resultText")
                onResult(resultText)
            }
            .addOnFailureListener { e ->
                onError(e)
            }
    } catch (e: Exception) {
        onError(e)
    }
}
