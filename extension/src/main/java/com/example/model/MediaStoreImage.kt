package com.example.model

import android.net.Uri
import java.util.Date

data class MediaStoreImage(
    val id: Long,
    val displayName: String,
    val dateAdded: Date? = null,
    val contentUri: Uri,
    val size: Long? = null,
    val author: String? = null,
    val lat: Double? = null,
    val long: Double? = null
)
