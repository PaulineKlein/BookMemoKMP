package com.pklein.bookmemokmp.scanner

actual class BarcodeScanner {
    actual fun scan(
        onResult: (String) -> Unit,
        onNotFoundException: () -> Unit,
        onError: () -> Unit,
    ) = Unit
}
