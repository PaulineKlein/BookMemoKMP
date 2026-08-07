package com.pklein.bookmemokmp.wear

import android.content.Context
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.pklein.bookmemokmp.domain.model.CollectionItem
import com.pklein.bookmemokmp.domain.model.ItemType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import org.json.JSONArray

const val WEAR_FAVORITES_PATH = "/favorites"
const val WEAR_FAVORITES_KEY = "favorites_json"

class WearFavoritesRepository(private val context: Context) {

    fun getFavorites(): Flow<List<CollectionItem>> =
        callbackFlow {
            val dataClient = Wearable.getDataClient(context)

            // Emit current value immediately if already present, empty list otherwise
            val existing = dataClient.getDataItems(
                android.net.Uri.parse("wear://*$WEAR_FAVORITES_PATH")
            ).await()
            var found = false
            existing.forEach { item ->
                val json = DataMapItem.fromDataItem(item)
                    .dataMap.getString(WEAR_FAVORITES_KEY)
                if (json != null) {
                    trySend(parseJson(json))
                    found = true
                }
            }
            existing.release()
            if (!found) trySend(emptyList())

            // Listen for updates
            val listener = DataClient.OnDataChangedListener { buffer: DataEventBuffer ->
                buffer.forEach { event ->
                    if (event.type == DataEvent.TYPE_CHANGED &&
                        event.dataItem.uri.path == WEAR_FAVORITES_PATH
                    ) {
                        val json = DataMapItem.fromDataItem(event.dataItem)
                            .dataMap.getString(WEAR_FAVORITES_KEY)
                        if (json != null) trySend(parseJson(json))
                    }
                }
                buffer.release()
            }
            dataClient.addListener(listener)
            awaitClose { dataClient.removeListener(listener) }
        }
}

private fun parseJson(json: String): List<CollectionItem> {
    val array = JSONArray(json)
    return buildList {
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            add(
                CollectionItem(
                    id = obj.getLong("id"),
                    type = ItemType.valueOf(obj.getString("type")),
                    title = obj.getString("title"),
                    tome = if (obj.has("tome")) obj.getInt("tome") else null,
                    chapter = if (obj.has("chapter")) obj.getInt("chapter") else null,
                    season = if (obj.has("season")) obj.getInt("season") else null,
                    episode = if (obj.has("episode")) obj.getInt("episode") else null,
                ),
            )
        }
    }
}