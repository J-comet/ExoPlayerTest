package hs.project.exoplayertest.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import hs.project.exoplayertest.databinding.ItemPlayerBinding

class ViewPagerAdapter : androidx.recyclerview.widget.ListAdapter<String, ViewPagerAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<String?>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }
) {

    private var eventListener: OnRecyclerListener?= null

    interface OnRecyclerListener {
        fun onPlay(item: String, exoPlayer: ExoPlayer?)
    }

    fun setOnListener(listener: OnRecyclerListener) {
        this.eventListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, eventListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(private val itemBinding: ItemPlayerBinding, listener: OnRecyclerListener?) : RecyclerView.ViewHolder(itemBinding.root) {

        private var player: ExoPlayer? = null

//        private var playWhenReady = true
//        private var currentWindow = 0
//        private var playbackPosition = 0L

        init {
            itemBinding.root.setOnClickListener {
                listener?.onPlay(currentList[bindingAdapterPosition], player)
            }
        }

        fun bind(data: String) {
            initializePlayer(data)
        }

//        private fun releasePlayer() {
//            player?.run {
//                playbackPosition = this.currentPosition
//                currentWindow = this.currentMediaItemIndex
//                playWhenReady = this.playWhenReady
//                release()
//            }
//            player = null
//        }

        private fun initializePlayer(path: String) {
            player = ExoPlayer.Builder(itemBinding.root.context).build().also {
                val mediaItem = MediaItem.fromUri(path)
                it.setMediaItem(mediaItem)
            }
            itemBinding.videoView.player = player

        }
    }


}