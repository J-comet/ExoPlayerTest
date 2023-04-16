package hs.project.exoplayertest.recyclerview

data class RecyclerItem(
    val videoNo: Int,
    val path: String,
    val playWhenReady: Boolean,
    val currentWindow: Int,
    val playbackPosition: Long
)
