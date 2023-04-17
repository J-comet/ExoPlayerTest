package hs.project.exoplayertest.custom_recycler

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.exoplayer.model.MediaObject
import hs.project.exoplayertest.R

class PlayerViewHolder(private val parent: View) : ViewHolder(
    parent
) {
    /**
     * below view have public modifier because
     * we have access PlayerViewHolder inside the ExoPlayerRecyclerView
     */
    @JvmField
    var mediaContainer: FrameLayout
    @JvmField
    var mediaCoverImage: ImageView
    @JvmField
    var volumeControl: ImageView
    @JvmField
    var progressBar: ProgressBar

//    var requestManager: RequestManager? = null
    private val title: TextView
    private val userHandle: TextView
    fun onBind(mediaObject: MediaObject) {
//        this.requestManager = requestManager
        parent.tag = this
        title.text = mediaObject.title
        userHandle.text = mediaObject.userHandle
//        this.requestManager!!.load(mediaObject.coverUrl)
//            .into(mediaCoverImage)
    }

    init {
        mediaContainer = itemView.findViewById(R.id.mediaContainer)
        mediaCoverImage = itemView.findViewById(R.id.ivMediaCoverImage)
        title = itemView.findViewById(R.id.tvTitle)
        userHandle = itemView.findViewById(R.id.tvUserHandle)
        progressBar = itemView.findViewById(R.id.progressBar)
        volumeControl = itemView.findViewById(R.id.ivVolumeControl)
    }
}