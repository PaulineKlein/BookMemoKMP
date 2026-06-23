package com.pklein.bookmemokmp.scanner

expect class BarcodeScanner {
    fun scan(
        onResult: (String) -> Unit,
        onNotFoundException: () -> Unit,
        onError: () -> Unit,
    )
}
