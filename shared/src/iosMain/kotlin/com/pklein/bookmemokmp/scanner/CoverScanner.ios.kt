package com.pklein.bookmemokmp.scanner

actual class CoverScanner {
    actual fun scan(
        onResult: (String) -> Unit,
        onError: () -> Unit,
    ) = Unit
}
