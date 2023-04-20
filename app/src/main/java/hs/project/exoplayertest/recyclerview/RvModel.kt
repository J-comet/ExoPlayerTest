package hs.project.exoplayertest.recyclerview

import android.graphics.Bitmap

data class RvModel(
    val id: Int,
    val videoPath: String,
    val innerInfo: RvModelInnerInfo
)

data class RvModelInnerInfo(
    val seekTime: Long,
    val defaultThumbnail: String,
    val thumbnail: Bitmap?,
    val isPlay: Boolean,
    val isEnd: Boolean
)

