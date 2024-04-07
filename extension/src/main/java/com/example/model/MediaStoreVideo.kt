package com.example.model

import android.net.Uri
import java.util.Date

data class MediaStoreVideo(
    val id: Long,
    val displayName: String,
    val dateAdded: Date? = null,
    val contentUri: Uri,
    val size: Long? = null,
    val duration: Long? = null,
    val lat: Double? = null,
    val long: Double? = null
)

