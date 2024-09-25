package com.example.invoicemanagergrupotorres.data.models

import java.io.Serializable
import java.util.Date

data class InvoiceData(
    val amount: Double = 0.0,
    val date: Date = Date(),
    val issuer: String = "",
    val reason: String = "",
    val iva: Double = 0.0,
    val invoiceType: String = "",
    val pdfUrl: String? = null
) : Serializable
