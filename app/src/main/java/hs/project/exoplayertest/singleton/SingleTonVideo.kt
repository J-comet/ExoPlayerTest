package hs.project.exoplayertest.singleton

import android.graphics.Bitmap

data class SingleTonVideo(
    val id: Int,
    val path: String,
    val playWhenReady: Boolean,
    var seekTime: Long,
    val defaultThumbnail: String,
    var videoThumbnail: Bitmap?
)
