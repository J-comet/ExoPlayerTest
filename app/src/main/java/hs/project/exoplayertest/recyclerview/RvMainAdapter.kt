package hs.project.exoplayertest.recyclerview

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import hs.project.exoplayertest.R
import hs.project.exoplayertest.databinding.ItemRvMainBinding

class RvMainAdapter(private val callback: Callback) :
    RecyclerView.Adapter<RvMainAdapter.ViewHolder>() {
    var datas = ArrayList<RvModel>()
    var viewHolders = ArrayList<RvMainAdapter.ViewHolder>()
    var exoPlayer: ExoPlayer? = null

    interface Callback {
        fun callback(selected: RvModel)
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        Log.e("33", "Attached")
        holder.bind(datas[holder.bindingAdapterPosition])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRvMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(datas[position])
        viewHolders.add(holder)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        Log.e("33", "Detached")
        holder.itemBinding.playerView.player?.release()
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    inner class ViewHolder(val itemBinding: ItemRvMainBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: RvModel) {

            Log.e("item", "item / $item")

            if (item.playWhenReady) {
                itemBinding.ivPlayPause.setImageResource(R.drawable.icn_live_video_pause)
            } else {
                itemBinding.ivPlayPause.setImageResource(R.drawable.icn_live_video_play)
            }

            itemBinding.ivPlayPause.setOnClickListener {
                callback.callback(datas[bindingAdapterPosition].copy(playbackPosition = itemBinding.playerView.player?.currentPosition ?: 0L, playWhenReady = itemBinding.playerView.player?.playWhenReady ?: false))
            }

            itemBinding.playerView.setOnClickListener {
                showBtnPlayPause()
            }


            exoPlayer = ExoPlayer.Builder(itemBinding.root.context).build().also {
                val mediaItem = MediaItem.fromUri(item.videoPath)
                it.setMediaItem(mediaItem)
                it.playWhenReady = item.playWhenReady
                it.seekTo(item.playbackPosition)
                it.prepare()

                it.addListener(object : Player.Listener {
                    override fun onRenderedFirstFrame() {
                        super.onRenderedFirstFrame()

                        // 첫 프레임이 그려진 뒤에 썸네일 이미지뷰를 없앤다. (재생 전 깜빡이는 현상 방지)
                        itemBinding.ivThumbnail.isVisible = false
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        when (playbackState) {
                            Player.STATE_READY -> {
                                // playWhenReady == true 이면 미디어 재생이 시작
                                // playWhenReady == false 이면 미디어 일시중지
                                // 즉시 재생 불가능, 준비만 된 상태
                                Log.e("STATE_READY", "준비")
                                itemBinding.progress.isVisible = false
                                itemBinding.ivPlayPause.isVisible = true
                            }
                            Player.STATE_ENDED -> {
                                // 재생 완료
                            }
                            Player.STATE_BUFFERING ->{
                                itemBinding.ivPlayPause.isVisible = false
                                itemBinding.progress.isVisible = true
                            }
                            Player.STATE_IDLE -> {
                                itemBinding.ivPlayPause.isVisible = false
                                itemBinding.progress.isVisible = true
                            }
                            else -> Unit
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        if (isPlaying) {
                            hideBtnPlayPause()
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        super.onPlayerError(error)
                        Log.e("ERROR", "ERROR / ".plus(error.localizedMessage.toString()))
                    }
                })
            }
            itemBinding.playerView.player = exoPlayer
        }

        private fun hideBtnPlayPause() {
            Handler(Looper.getMainLooper()).postDelayed({
                itemBinding.ivPlayPause.isVisible = false
            }, 3000)
        }

        private fun showBtnPlayPause() {
            itemBinding.ivPlayPause.isVisible = true
            Handler(Looper.getMainLooper()).postDelayed({
                itemBinding.ivPlayPause.isVisible = false
            }, 3000)
        }

    }
}