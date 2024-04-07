package com.example.extension.worker

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import com.example.extension.objects.dateToTimestamp
import com.example.model.MediaStoreImage
import com.example.model.MediaStoreVideo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.concurrent.TimeUnit


suspend fun getListImagesMediaStore(
    ioDispatcher: CoroutineDispatcher,
    contentResolver: ContentResolver,
    projection: Array<String>? = getBaseImageProjection(),
    selection: String? = getBaseImageSelection(),
    selectionArgs: Array<String>? = getBaseImageSelectionArgs(),
    sortOrder: String? = getBaseImageSortOrder()
): MutableList<MediaStoreImage> {
    val images = mutableListOf<MediaStoreImage>()

    withContext(ioDispatcher) {
        /**
         * projection is used to get the properties of Media
         */

        /**
         * The `selection` is the "WHERE ..." clause of a SQL statement. It's also possible
         * to omit this by passing `null` in its place, and then all rows will be returned.
         * In this case we're using a selection based on the date the image was taken.
         *
         * Note that we've included a `?` in our selection. This stands in for a variable
         * which will be provided by the next variable.
         */

        /**
         * The `selectionArgs` is a list of values that will be filled in for each `?`
         * in the `selection`.
         */

        /**
         * Sort order to use. This can also be null, which will use the default sort
         * order. For [MediaStore.Images], the default sort order is ascending by date taken.
         */

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->

            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateModifiedColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val sizeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

            logd("Found ${cursor.count} images")
            while (cursor.moveToNext()) {

                // Here we'll use the column indexs that we found above.
                val id = cursor.getLong(idColumn)
                val dateModified =
                    Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateModifiedColumn)))
                val displayName = cursor.getString(displayNameColumn)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val size = cursor.getLong(sizeColumn)

                val latLong = getLatLongMedia(
                    contentResolver = contentResolver,
                    uri = contentUri,
                    cursor = cursor,
                    MediaStoreType.IMAGE
                )

                val image = MediaStoreImage(
                    id = id,
                    displayName = displayName,
                    dateAdded = dateModified,
                    contentUri = contentUri,
                    size = size,
                    lat = latLong[0],
                    long = latLong[1]
                )
                images += image

                // For debugging, we'll output the image objects we create to logcat.
                logd("Added image: $image")
            }
        }
    }

    logd("Found ${images.size} images")
    return images
}

fun getBaseImageProjection() = arrayOf(
    MediaStore.Images.Media._ID,
    MediaStore.Images.Media.DISPLAY_NAME,
    MediaStore.Images.Media.DATE_ADDED,
    MediaStore.Images.Media.SIZE
)

fun getBaseImageSelection() = "${MediaStore.Images.Media.DATE_ADDED} >= ?"

fun getBaseImageSelectionArgs() = arrayOf(
    // Release day of the G1. :)
    dateToTimestamp(day = 22, month = 10, year = 2008).toString()
)

fun getBaseImageSortOrder() = "${MediaStore.Images.Media.DATE_ADDED} DESC"

suspend fun getListVideoMediaStore(
    ioDispatcher: CoroutineDispatcher,
    contentResolver: ContentResolver,
    projection: Array<String>? = getBaseVideoProjection(),
    selection: String? = getBaseVideoSelection(),
    selectionArgs: Array<String>? = getBaseVideoSelectionArgs(),
    sortOrder: String? = getBaseVideoSortOrder()
): MutableList<MediaStoreVideo> {
    val videos = mutableListOf<MediaStoreVideo>()

    withContext(ioDispatcher) {

        contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->

            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val dateModifiedColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val sizeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val duration = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)

            logd("Found ${cursor.count} Video")
            while (cursor.moveToNext()) {

                // Here we'll use the column indexs that we found above.
                val id = cursor.getLong(idColumn)
                val dateModified =
                    Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateModifiedColumn)))
                val displayName = cursor.getString(displayNameColumn)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val size = cursor.getLong(sizeColumn)

                val latLong = getLatLongMedia(
                    contentResolver = contentResolver,
                    uri = contentUri,
                    cursor = cursor,
                    MediaStoreType.VIDEO
                )

                val durationString = cursor.getLong(duration)

                val video =
                    MediaStoreVideo(
                        id,
                        displayName,
                        dateModified,
                        contentUri,
                        size,
                        durationString,
                        lat = latLong[0],
                        long = latLong[1]
                    )
                videos += video

                // For debugging, we'll output the image objects we create to logcat.
                logd("Added image: $video")
            }
        }
    }

    logd("Found ${videos.size} Videos")
    return videos
}

enum class MediaStoreType {
    IMAGE,
    VIDEO
}

fun getBaseVideoProjection() = arrayOf(
    MediaStore.Video.Media._ID,
    MediaStore.Video.Media.DISPLAY_NAME,
    MediaStore.Video.Media.DATE_ADDED,
    MediaStore.Video.Media.SIZE,
    MediaStore.Video.Media.DURATION,
)

fun getBaseVideoSelection() = "${MediaStore.Video.Media.DATE_ADDED} >= ?"

fun getBaseVideoSelectionArgs() = arrayOf(
    // Release day of the G1. :)
    dateToTimestamp(day = 22, month = 10, year = 2008).toString()
)

fun getBaseVideoSortOrder() = "${MediaStore.Video.Media.DATE_ADDED} DESC"

@Suppress("DEPRECATION")
fun getLatLongMedia(
    contentResolver: ContentResolver,
    uri: Uri,
    cursor: Cursor,
    mediaStoreType: MediaStoreType
): DoubleArray {
    var latLong = doubleArrayOf(0.0, 0.0)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val mediaUri = MediaStore.setRequireOriginal(uri)
        contentResolver.openInputStream(mediaUri)?.use { stream ->
            ExifInterface(stream).run {
                // If lat/long is null, fall back to the coordinates (0, 0).
                latLong = this.latLong ?: latLong
            }
        }

    } else {
        when (mediaStoreType) {
            MediaStoreType.IMAGE -> {
                latLong[0] =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.LATITUDE).toDouble()
                latLong[1] =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.LONGITUDE).toDouble()
            }

            MediaStoreType.VIDEO -> {
                latLong[0] =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.LATITUDE).toDouble()
                latLong[1] =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.LONGITUDE).toDouble()
            }
        }
    }

    return latLong
}
