package com.pklein.bookmemokmp.wear

import android.content.Context
import android.net.Uri
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.pklein.bookmemokmp.domain.model.CollectionItem
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject

const val WEAR_FAVORITES_PATH = "/favorites"
const val WEAR_FAVORITES_KEY = "favorites_json"

suspend fun pushFavoritesToWear(context: Context, favorites: List<CollectionItem>) {
    val json = JSONArray().apply {
        favorites.forEach { item ->
            put(JSONObject().apply {
                put("id", item.id)
                put("type", item.type.name)
                put("title", item.title)
                item.tome?.let { put("tome", it) }
                item.chapter?.let { put("chapter", it) }
                item.season?.let { put("season", it) }
                item.episode?.let { put("episode", it) }
            })
        }
    }.toString()

    val request = PutDataMapRequest.create(WEAR_FAVORITES_PATH).apply {
        dataMap.putString(WEAR_FAVORITES_KEY, json)
        // Force update even if list is identical (timestamp as change marker)
        dataMap.putLong("timestamp", System.currentTimeMillis())
    }
    Wearable.getDataClient(context)
        .putDataItem(request.asPutDataRequest().setUrgent())
        .await()
}