package hs.project.exoplayertest.recylerview_2

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import hs.project.exoplayertest.databinding.ItemVideoTest02Binding

class RecycleTest02Adapter(
    private val playChange: (isPlay: Boolean, item: TestVideo02) -> Unit
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
        holder.exoPlayer?.release()
        super.onViewRecycled(holder)
    }

    inner class ViewHolder(private val itemBinding: ItemVideoTest02Binding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        var exoPlayer: ExoPlayer? = null

        fun bind(item: TestVideo02) {
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
                                    // 재생 완료
                                }

                                Player.STATE_BUFFERING -> {
                                }

                                Player.STATE_IDLE -> {
                                }

                                else -> Unit
                            }
                        }

                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            super.onIsPlayingChanged(isPlaying)
                            getCurrentPlayerPosition()
                            playChange(isPlaying, item)
                        }
                    })
                    it.prepare()
                }

        }

        // 폴딩 방식으로
        private fun getCurrentPlayerPosition() {
            Log.d("TAG", "current pos: " + exoPlayer?.currentPosition)
            if (exoPlayer?.isPlaying == true) {
                itemBinding.playerView.postDelayed({ getCurrentPlayerPosition() }, 1000)
            }
        }
    }
}
