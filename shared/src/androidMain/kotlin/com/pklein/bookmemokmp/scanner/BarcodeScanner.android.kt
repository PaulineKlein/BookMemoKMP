package com.pklein.bookmemokmp.scanner

import android.app.Activity
import android.content.ActivityNotFoundException
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

actual class BarcodeScanner(private val activity: Activity) {
    actual fun scan(onResult: (String) -> Unit, onError: () -> Unit) {
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8)
            .build()
        val scanner = GmsBarcodeScanning.getClient(activity, options)
        try {
            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    barcode.rawValue?.let(onResult) ?: onError()
                }
                .addOnFailureListener { onError() }
                .addOnCanceledListener { onError() }
        } catch (e: ActivityNotFoundException) {
            onError()
        }
    }
}
