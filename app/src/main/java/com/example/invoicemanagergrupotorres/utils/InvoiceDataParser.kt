package com.example.invoicemanagergrupotorres.utils

import android.util.Log
import com.example.invoicemanagergrupotorres.data.models.InvoiceData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

fun parseInvoiceText(recognizedText: String): InvoiceData {
    var amount = 0.0
    var date = Date()
    var issuer = ""
    var reason = ""
    var iva = 0.0
    var invoiceType = ""

    // Ejemplo simple de cómo extraer el monto total
    val amountPattern = Pattern.compile("(Total|TOTAL)\\s*[:\\-]?\\s*\\$?([0-9]+\\.?[0-9]*)")
    val amountMatcher = amountPattern.matcher(recognizedText)
    if (amountMatcher.find()) {
        amount = amountMatcher.group(2)?.toDoubleOrNull() ?: 0.0
    }

    // Ejemplo de cómo extraer la fecha
    val datePattern = Pattern.compile("(Fecha|FECHA)\\s*[:\\-]?\\s*([0-9]{2}/[0-9]{2}/[0-9]{4})")
    val dateMatcher = datePattern.matcher(recognizedText)
    if (dateMatcher.find()) {
        val dateString = dateMatcher.group(2)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        date = dateFormat.parse(dateString) ?: Date()
    }

    // Ejemplo de cómo extraer el emisor
    val issuerPattern = Pattern.compile("(Emisor|EMISOR)\\s*[:\\-]?\\s*(.+)")
    val issuerMatcher = issuerPattern.matcher(recognizedText)
    if (issuerMatcher.find()) {
        issuer = issuerMatcher.group(2)?.trim() ?: ""
    }

    // Ejemplo de cómo extraer el IVA
    val ivaPattern = Pattern.compile("(IVA|iva)\\s*[:\\-]?\\s*\\$?([0-9]+\\.?[0-9]*)")
    val ivaMatcher = ivaPattern.matcher(recognizedText)
    if (ivaMatcher.find()) {
        iva = ivaMatcher.group(2)?.toDoubleOrNull() ?: 0.0
    }

    // Ejemplo de cómo extraer el concepto de la factura
    val conceptPattern = Pattern.compile("(Concepto|CONCEPTO)\\s*[:\\-]?\\s*(.+)")
    val conceptMatcher = conceptPattern.matcher(recognizedText)
    if (conceptMatcher.find()) {
        reason = conceptMatcher.group(2)?.trim() ?: ""
    }

    // Ejemplo de cómo extraer el tipo de factura
    val tipoFacturaPattern = Pattern.compile("(Tipo de Factura|TIPO DE FACTURA)\\s*[:\\-]?\\s*(Emitida|Recibida|EMITIDA|RECIBIDA)")
    val tipoFacturaMatcher = tipoFacturaPattern.matcher(recognizedText)
    if (tipoFacturaMatcher.find()) {
        invoiceType = tipoFacturaMatcher.group(2)?.trim() ?: ""
    }

    // Log para depuración
    Log.d("parseInvoiceText", "Amount extracted: $amount")
    Log.d("parseInvoiceText", "Date extracted: $date")
    Log.d("parseInvoiceText", "Issuer extracted: $issuer")
    Log.d("parseInvoiceText", "IVA extracted: $iva")
    Log.d("parseInvoiceText", "Reason extracted: $reason")
    Log.d("parseInvoiceText", "Invoice Type extracted: $invoiceType")

    // Crear y devolver un objeto InvoiceData con los datos extraídos
    return InvoiceData(
        amount = amount,
        date = date,
        issuer = issuer,
        reason = reason,
        iva = iva,
        invoiceType = invoiceType
    )
}

