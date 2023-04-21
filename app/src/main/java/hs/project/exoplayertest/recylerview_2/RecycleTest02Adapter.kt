package hs.project.exoplayertest.recylerview_2

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextClock
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import hs.project.exoplayertest.R
import hs.project.exoplayertest.databinding.ItemVideoTest02Binding

class RecycleTest02Adapter(
    private val playChange: (isPlay: Boolean, item: TestVideo02) -> Unit,
    private val fullScreen: (item: TestVideo02) -> Unit
) : ListAdapter<TestVideo02, RecycleTest02Adapter.ViewHolder>(
    object : DiffUtil.ItemCallback<TestVideo02?>() {
        override fun areItemsTheSame(oldItem: TestVideo02, newItem: TestVideo02): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TestVideo02, newItem: TestVideo02): Boolean {
            return oldItem == newItem
        }
    }
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecycleTest02Adapter.ViewHolder {
        return ViewHolder(
            ItemVideoTest02Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecycleTest02Adapter.ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.releasePlayer()
        super.onViewRecycled(holder)
    }

    inner class ViewHolder(private val itemBinding: ItemVideoTest02Binding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        var exoPlayer: ExoPlayer? = null

        private lateinit var btnExoFullScreen: ImageView
        private lateinit var btnExoPlay: ImageButton
        private lateinit var btnExoPause: ImageView
        private lateinit var exoPlayerDim: View

        var seekTime = 0L
        var isPlay = false

//        init {
//            itemBinding.ivPlay.setOnClickListener {
//                playChange(true, currentList[bindingAdapterPosition])
//            }
//        }

        fun bind(item: TestVideo02) {
            btnExoPlay = itemBinding.playerView.findViewById(com.google.android.exoplayer2.ui.R.id.exo_play)
            btnExoPause = itemBinding.playerView.findViewById(com.google.android.exoplayer2.ui.R.id.exo_pause)
            btnExoFullScreen = itemBinding.playerView.findViewById(R.id.exo_fullscreen_icon)
            exoPlayerDim = itemBinding.playerView.findViewById(R.id.bg_exo_dim)

            btnExoFullScreen.setOnClickListener {
                updateSeekTime(item)
                fullScreen(item)
            }

            exoPlayer = ExoPlayer.Builder(itemBinding.root.context).build()
                .also {
                    itemBinding.playerView.player = it
                    it.setMediaItem(MediaItem.fromUri(item.path))
                    it.playWhenReady = item.playWhenReady
                    it.seekTo(item.seekTime)
                    it.addListener(object : Player.Listener {

                        override fun onPlaybackStateChanged(playbackState: Int) {
                            super.onPlaybackStateChanged(playbackState)
                            when (playbackState) {
                                Player.STATE_READY -> {
                                    // playWhenReady == true 이면 미디어 재생이 시작
                                    // playWhenReady == false 이면 미디어 일시중지
                                    // 즉시 재생 불가능, 준비만 된 상태
                                    Log.e("STATE_READY", "준비")

//                                    if (item.playWhenReady) {
//                                        exoPlayer?.play()
//                                    } else {
//                                        exoPlayer?.pause()
//                                    }
                                }

                                Player.STATE_ENDED -> {
//                                    item.seekTime = exoPlayer?.currentPosition ?: 0L
                                }

                                Player.STATE_BUFFERING -> {
                                }

                                Player.STATE_IDLE -> {
//                                    item.seekTime = exoPlayer?.currentPosition ?: 0L
                                }

                                else -> Unit
                            }
                        }

                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            super.onIsPlayingChanged(isPlaying)
                            playChange(isPlaying, item)
                            getCurrentPlayerPosition()
                        }
                    })
                    it.prepare()
                }

        }

        fun releasePlayer() {
            exoPlayer?.run {
//                seekTime = this.currentPosition
//                isPlay = this.playWhenReady
                release()
            }
            exoPlayer = null
        }

        // 폴딩 방식으로
        private fun getCurrentPlayerPosition() {
            Log.d("TAG", "current pos: " + exoPlayer?.currentPosition)
            if (exoPlayer?.isPlaying == true) {
                updateSeekTime(currentList[bindingAdapterPosition])
                itemBinding.playerView.postDelayed({ getCurrentPlayerPosition() }, 1000)
            }
        }

        private fun updateSeekTime(item: TestVideo02) {
            item.seekTime = exoPlayer?.currentPosition ?: 0L
        }
    }
}
