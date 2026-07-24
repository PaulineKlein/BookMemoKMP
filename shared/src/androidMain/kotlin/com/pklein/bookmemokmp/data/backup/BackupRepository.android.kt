package com.pklein.bookmemokmp.data.backup

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.FormatType
import com.pklein.bookmemokmp.domain.model.ItemType
import com.pklein.bookmemokmp.domain.model.MangaApiType
import com.pklein.bookmemokmp.domain.repository.IBackupRepository
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

private const val COLLECTION_NAME = "backups"
private const val TIMESTAMP_FIELD = "timestamp"
private const val ITEMS_FIELD = "items"

class BackupRepository(
    private val context: Context,
) : IBackupRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun getCurrentUserEmail(): String? = auth.currentUser?.email

    override suspend fun signIn(webClientId: String) {
        val credentialManager = CredentialManager.create(context)
        val request =
            GetCredentialRequest
                .Builder()
                .addCredentialOption(
                    GetGoogleIdOption
                        .Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(webClientId)
                        .build(),
                ).build()
        val result = credentialManager.getCredential(context, request)
        val idToken = GoogleIdTokenCredential.createFrom(result.credential.data).idToken
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).await()
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun fetchLastBackupDate(): String? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val doc =
                withTimeout(10.seconds) {
                    firestore
                        .collection(COLLECTION_NAME)
                        .document(uid)
                        .get()
                        .await()
                }
            val ts = doc.getLong(TIMESTAMP_FIELD) ?: return null
            formatTimestamp(ts)
        } catch (_: Exception) {
            null
        }
    }

    private fun formatTimestamp(ms: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        return sdf.format(java.util.Date(ms))
    }

    override suspend fun backup(items: List<CollectionItem>) {
        val uid = auth.currentUser?.uid ?: error("Not signed in")
        val data =
            hashMapOf(
                ITEMS_FIELD to items.map { it.toMap() },
                TIMESTAMP_FIELD to System.currentTimeMillis(),
            )
        withTimeout(15.seconds) {
            firestore
                .collection(COLLECTION_NAME)
                .document(uid)
                .set(data)
                .await()
        }
    }

    override suspend fun restore(): List<CollectionItem> {
        val uid = auth.currentUser?.uid ?: error("Not signed in")
        val doc =
            withTimeout(15.seconds) {
                firestore
                    .collection(COLLECTION_NAME)
                    .document(uid)
                    .get()
                    .await()
            }

        @Suppress("UNCHECKED_CAST")
        val rawItems = doc.get(ITEMS_FIELD) as? List<Map<String, Any?>> ?: return emptyList()
        return rawItems.mapNotNull { it.toCollectionItem() }
    }

    // ── Serialization ─────────────────────────────────────────────────────────

    private fun CollectionItem.toMap(): Map<String, Any?> =
        mapOf(
            "type" to type.name,
            "title" to title,
            "author" to author,
            "illustrator" to illustrator,
            "year" to year,
            "bought" to bought,
            "wishlist" to wishlist,
            "finished" to finished,
            "tome" to tome,
            "chapter" to chapter,
            "episode" to episode,
            "season" to season,
            "description" to description,
            "favorite" to favorite,
            "imageUrl" to imageUrl,
            "isBorrowed" to isBorrowed,
            "borrowedSince" to borrowedSince,
            "borrowedBy" to borrowedBy,
            "mangaApiId" to mangaApiId,
            "mangaApiAuthorId" to mangaApiAuthorId,
            "mangaApiType" to mangaApiType?.name,
            "totTome" to totTome,
            "totChapter" to totChapter,
            "totEpisode" to totEpisode,
            "checkedTomes" to checkedTomes,
            "notes" to notes,
            "format" to format?.name,
            "isDigital" to isDigital,
        )

    @Suppress("UNCHECKED_CAST")
    private fun Map<String, Any?>.toCollectionItem(): CollectionItem? {
        val title = this["title"] as? String ?: return null
        val type = ItemType.fromString(this["type"] as? String ?: return null)
        return CollectionItem(
            type = type,
            title = title,
            author = this["author"] as? String,
            illustrator = this["illustrator"] as? String,
            year = (this["year"] as? Long)?.toInt(),
            bought = this["bought"] as? Boolean ?: false,
            wishlist = this["wishlist"] as? Boolean ?: false,
            finished = this["finished"] as? Boolean ?: false,
            tome = (this["tome"] as? Long)?.toInt(),
            chapter = (this["chapter"] as? Long)?.toInt(),
            episode = (this["episode"] as? Long)?.toInt(),
            season = (this["season"] as? Long)?.toInt(),
            description = this["description"] as? String,
            favorite = this["favorite"] as? Boolean ?: false,
            imageUrl = this["imageUrl"] as? String,
            isBorrowed = this["isBorrowed"] as? Boolean ?: false,
            borrowedSince = this["borrowedSince"] as? Long,
            borrowedBy = this["borrowedBy"] as? String,
            mangaApiId = this["mangaApiId"] as? Long,
            mangaApiAuthorId = this["mangaApiAuthorId"] as? Long,
            mangaApiType = MangaApiType.fromString(this["mangaApiType"] as? String),
            totTome = (this["totTome"] as? Long)?.toInt(),
            totChapter = (this["totChapter"] as? Long)?.toInt(),
            totEpisode = (this["totEpisode"] as? Long)?.toInt(),
            checkedTomes = (this["checkedTomes"] as? List<Long>)?.map { it.toInt() } ?: emptyList(),
            notes = this["notes"] as? String,
            format = FormatType.fromString(this["format"] as? String),
            isDigital = this["isDigital"] as? Boolean ?: false,
        )
    }
}
