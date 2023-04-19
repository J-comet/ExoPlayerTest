package hs.project.exoplayertest.live_test

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import hs.project.exoplayertest.databinding.ItemLiveVideoViewpagerBinding
import hs.project.exoplayertest.live_test.model.LiveVideoViewPager


class LiveTestViewpagerAdapter(private var exoPlayer: ExoPlayer?) :
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
        holder.exoPlayerRelease()
//        holder.itemBinding.playerView.player?.release()
    }

    inner class ViewHolder(
        private val itemBinding: ItemLiveVideoViewpagerBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: LiveVideoViewPager) {
            initPlayer(item.videoPath)
        }

        private fun initPlayer(path: String) {

            Log.e("VIEWPAGER", "$exoPlayer")

            exoPlayer?.also {
                val mediaItem = MediaItem.fromUri(path)
                it.setMediaItem(mediaItem)
                it.playWhenReady = false
                it.addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        super.onPlayerError(error)
                        Log.e("VIEWPAGER", error.message.toString())
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        when (playbackState) {
                            Player.STATE_READY -> {
                                // playWhenReady == true 이면 미디어 재생이 시작
                                // playWhenReady == false 이면 미디어 일시중지
                                // 즉시 재생 불가능, 준비만 된 상태
                                Log.e("VIEWPAGER", "STATE_READY")
                                exoPlayer?.play()
                            }

                            Player.STATE_ENDED -> {
                                // 재생 완료
                                Log.e("VIEWPAGER", "STATE_ENDED")
                            }

                            Player.STATE_BUFFERING -> {
                                Log.e("VIEWPAGER", "STATE_BUFFERING")
                            }

                            Player.STATE_IDLE -> {
                                Log.e("VIEWPAGER", "STATE_IDLE")
                            }

                            else -> Unit
                        }
                    }
                })
            }
            itemBinding.playerView.player = exoPlayer
        }

        fun exoPlayerRelease() {
            exoPlayer?.let {
                it.release()
                exoPlayer = null
            }
        }
    }
}