package com.pklein.bookmemokmp.scanner

expect class CoverScanner {
    fun scan(
        onResult: (String) -> Unit,
        onError: () -> Unit,
    )
}
