package hs.project.exoplayertest.recyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import hs.project.exoplayertest.databinding.ItemRvMainBinding

class RvMainAdapter(private val callback: Callback) :
    RecyclerView.Adapter<RvMainAdapter.ViewHolder>() {
    var datas = ArrayList<RvModel>()
    var viewHolders = ArrayList<RvMainAdapter.ViewHolder>()
    var exoPlayer: ExoPlayer? = null

    interface Callback {
        fun callback(selected: RvModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRvMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position], position)
        viewHolders.add(holder)
//        viewHolders.add(holder)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    inner class ViewHolder(val itemBinding: ItemRvMainBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: RvModel, position: Int) {

            Log.e("item", "item / $item")

            exoPlayer = ExoPlayer.Builder(itemBinding.root.context).build().also {
                val mediaItem = MediaItem.fromUri(item.videoPath)
                it.setMediaItem(mediaItem)
//                it.playWhenReady = item.playWhenReady
                it.seekTo(item.playbackPosition)
                it.prepare()
                it.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        when (playbackState) {
                            Player.STATE_READY -> {
                                // playWhenReady == true 이면 미디어 재생이 시작
                                // playWhenReady == false 이면 미디어 일시중지
                                // 즉시 재생 불가능, 준비만 된 상태
                                Log.e("STATE_READY", "준비")
                            }
                            Player.STATE_ENDED -> {
                                // 재생 완료
                            }
                            Player.STATE_BUFFERING ->{
                            }
                            Player.STATE_IDLE -> {
                            }
                            else -> Unit
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)

                        /**
                         * 현재 무한 루프 도는 중
                         */
                        if (isPlaying) {
                            callback.callback(datas[position])
                        }

//                        if (isPlaying) {
//                            binding.videoView2.player?.playWhenReady = false
//                            binding.videoView3.player?.playWhenReady = false
//                        } else {
//                            playWhenReady = it.playWhenReady
//                            playbackPosition = it.currentPosition
//                        }

                    }

                    override fun onPlayerError(error: PlaybackException) {
                        super.onPlayerError(error)
                        Log.e("ERROR", "ERROR".plus(error.localizedMessage.toString()))
                    }
                })
            }

            itemBinding.playerView.player = exoPlayer

        }
    }
}