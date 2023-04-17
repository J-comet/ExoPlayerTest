package hs.project.exoplayertest.recyclerview

data class RecyclerItem(
    val videoNo: Int,
    val playWhenReady: Boolean,
    val currentWindow: Int,
    val playbackPosition: Long,
    val videoPaths: ArrayList<String>,
)
