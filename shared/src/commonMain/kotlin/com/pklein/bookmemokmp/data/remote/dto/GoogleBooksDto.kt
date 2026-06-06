package com.pklein.bookmemokmp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class GoogleBooksResponse(
    val items: List<GoogleBooksVolume>? = null
)

@Serializable
internal data class GoogleBooksVolume(
    val volumeInfo: VolumeInfo = VolumeInfo()
)

@Serializable
internal data class VolumeInfo(
    val title: String = "",
    val authors: List<String>? = null,
    val publishedDate: String? = null,
    val description: String? = null,
    val imageLinks: ImageLinks? = null
)

@Serializable
internal data class ImageLinks(
    val smallThumbnail: String? = null,
    val thumbnail: String? = null
)
