package com.example.invoicemanagergrupotorres.viewmodel

import androidx.lifecycle.ViewModel
import com.example.invoicemanagergrupotorres.data.models.InvoiceData

class SharedViewModel : ViewModel() {
    var selectedInvoice: InvoiceData? = null
}
