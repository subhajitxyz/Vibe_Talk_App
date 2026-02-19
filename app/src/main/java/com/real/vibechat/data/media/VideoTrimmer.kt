package com.real.vibechat.data.media

import android.content.Context
import androidx.media3.common.MediaItem
import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class VideoTrimmer @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun trimTo10Seconds(inputUri: Uri): Uri = suspendCancellableCoroutine { cont ->

        val outputFile = File(
            context.cacheDir,
            "trimmed_${System.currentTimeMillis()}.mp4"
        )

        val transformer = Transformer.Builder(context).build()

        val mediaItem = MediaItem.Builder()
            .setUri(inputUri)
            .setClippingConfiguration(
                MediaItem.ClippingConfiguration.Builder()
                    .setStartPositionMs(0)
                    .setEndPositionMs(10_000)
                    .build()
            )
            .build()

        val editedMediaItem = EditedMediaItem.Builder(mediaItem)
            .build()

        transformer.start(
            editedMediaItem,
            outputFile.absolutePath
        )

        transformer.addListener(object : Transformer.Listener {
            override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                cont.resume(outputFile.toUri())
            }

            override fun onError(
                composition: Composition,
                exportResult: ExportResult,
                exception: ExportException
            ) {
                cont.resumeWithException(exception)
            }
        })
    }
}
