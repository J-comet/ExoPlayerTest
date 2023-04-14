package hs.project.exoplayertest.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import hs.project.exoplayertest.R
import hs.project.exoplayertest.databinding.ItemRecyclerBinding

class RecyclerAdapter : androidx.recyclerview.widget.ListAdapter<RecyclerItem, RecyclerAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<RecyclerItem?>() {
        override fun areItemsTheSame(
            oldItem: RecyclerItem,
            newItem: RecyclerItem
        ): Boolean {
            return oldItem.videoNo == newItem.videoNo
        }

        override fun areContentsTheSame(
            oldItem: RecyclerItem,
            newItem: RecyclerItem
        ): Boolean {
            return oldItem == newItem
        }
    }
) {

    private var eventListener: OnRecyclerListener?= null

    interface OnRecyclerListener {
        fun onPlay(item: RecyclerItem)
    }

    fun setOnListener(listener: OnRecyclerListener) {
        this.eventListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, eventListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(private val itemBinding: ItemRecyclerBinding, listener: OnRecyclerListener?) : RecyclerView.ViewHolder(itemBinding.root) {

        private var player: ExoPlayer? = null

        private var playWhenReady = true
        private var currentWindow = 0
        private var playbackPosition = 0L

        init {
            itemBinding.root.setOnClickListener {
                listener?.onPlay(currentList[bindingAdapterPosition])
            }
        }

        fun bind(data: RecyclerItem) {
            initializePlayer(data.path)
        }

        private fun releasePlayer() {
            player?.run {
                playbackPosition = this.currentPosition
                currentWindow = this.currentMediaItemIndex
                playWhenReady = this.playWhenReady
                release()
            }
            player = null
        }

        private fun initializePlayer(path: String) {
            player = ExoPlayer.Builder(itemBinding.root.context)
                .build()
                .also { exoPlayer ->
                    itemBinding.videoView.player = exoPlayer
                    val mediaItem = MediaItem.fromUri(path)
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.playWhenReady = playWhenReady
                    exoPlayer.seekTo(currentWindow, playbackPosition)
                    exoPlayer.prepare()
                }
        }
    }


}