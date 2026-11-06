package com.pklein.bookmemokmp.scanner

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File

/** Must be created before the activity is STARTED (registers an activity result launcher). */
actual class CoverScanner(
    private val activity: ComponentActivity,
) {
    private var photoUri: Uri? = null
    private var onResult: ((String) -> Unit)? = null
    private var onError: (() -> Unit)? = null

    private val launcher =
        activity.registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            val uri = photoUri
            if (success && uri != null) recognize(uri) else clear()
        }

    actual fun scan(
        onResult: (String) -> Unit,
        onError: () -> Unit,
    ) {
        this.onResult = onResult
        this.onError = onError
        val file = File(activity.cacheDir, "cover_scan.jpg")
        val uri = FileProvider.getUriForFile(activity, "${activity.packageName}.fileprovider", file)
        photoUri = uri
        try {
            launcher.launch(uri)
        } catch (_: Exception) {
            fail()
        }
    }

    private fun recognize(uri: Uri) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        try {
            recognizer
                .process(InputImage.fromFilePath(activity, uri))
                .addOnSuccessListener { text ->
                    val title = extractTitle(text)
                    if (title.isNullOrBlank()) fail() else succeed(title)
                }.addOnFailureListener { fail() }
                .addOnCompleteListener { recognizer.close() }
        } catch (_: Exception) {
            recognizer.close()
            fail()
        }
    }

    // A cover title is usually the text printed in the biggest font: pick the block
    // whose lines have the greatest average height.
    private fun extractTitle(text: Text): String? =
        text.textBlocks
            .filter { block -> block.text.isNotBlank() }
            .maxByOrNull { block ->
                block.lines
                    .map { it.boundingBox?.height() ?: 0 }
                    .average()
                    .takeUnless { it.isNaN() } ?: 0.0
            }?.lines
            ?.joinToString(" ") { it.text.trim() }
            ?.replace(Regex("\\s+"), " ")
            ?.trim()

    private fun succeed(title: String) {
        val callback = onResult
        clear()
        callback?.invoke(title)
    }

    private fun fail() {
        val callback = onError
        clear()
        callback?.invoke()
    }

    private fun clear() {
        photoUri = null
        onResult = null
        onError = null
    }
}
