package hs.project.exoplayertest.recyclerview

data class RvModel(
    val id: Int,
    val videoPath: String,
    val playbackPosition: Long,
    val playWhenReady: Boolean
)
