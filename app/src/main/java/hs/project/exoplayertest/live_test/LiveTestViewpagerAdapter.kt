package hs.project.exoplayertest.live_test

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import hs.project.exoplayertest.databinding.ItemLiveVideoViewpagerBinding
import hs.project.exoplayertest.live_test.model.LiveVideoViewPager
import hs.project.exoplayertest.recyclerview.ViewPagerAdapter


class LiveTestViewpagerAdapter :
    ListAdapter<LiveVideoViewPager, LiveTestViewpagerAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<LiveVideoViewPager?>() {
            override fun areItemsTheSame(
                oldItem: LiveVideoViewPager,
                newItem: LiveVideoViewPager,
            ): Boolean {
                return oldItem.videoDetailNo == newItem.videoDetailNo
            }

            override fun areContentsTheSame(
                oldItem: LiveVideoViewPager,
                newItem: LiveVideoViewPager,
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLiveVideoViewpagerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
//        holder.itemBinding.playerView.player?.release()
    }

    inner class ViewHolder(
        val itemBinding: ItemLiveVideoViewpagerBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private var player: ExoPlayer? = null

        fun bind(item: LiveVideoViewPager) {
            initializePlayer(item.videoPath)
        }

        private fun initializePlayer(path: String) {
            player = ExoPlayer.Builder(itemBinding.root.context).build().also {
                val mediaItem = MediaItem.fromUri(path)
                it.setMediaItem(mediaItem)
                it.playWhenReady = false
            }
            itemBinding.playerView.player = player
        }
    }
}