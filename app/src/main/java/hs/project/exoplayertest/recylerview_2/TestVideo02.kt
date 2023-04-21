package hs.project.exoplayertest.recylerview_2

import android.graphics.Bitmap

data class TestVideo02(
    val id: Int,
    val path: String,
    val playWhenReady: Boolean,
    var seekTime: Long,
    val defaultThumbnail: String,
    var videoThumbnail: Bitmap?,
)
