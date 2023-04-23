package hs.project.exoplayertest.singleton

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import hs.project.exoplayertest.R
import hs.project.exoplayertest.databinding.ItemVideoTest02Binding
import hs.project.exoplayertest.util.ExoPlayerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SingleTonAdapter(
    private val playChange: (isPlay: Boolean, item: SingleTonVideo) -> Unit,
    private val fullScreen: (item: SingleTonVideo) -> Unit
) : ListAdapter<SingleTonVideo, SingleTonAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<SingleTonVideo?>() {
        override fun areItemsTheSame(oldItem: SingleTonVideo, newItem: SingleTonVideo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SingleTonVideo, newItem: SingleTonVideo): Boolean {
            return oldItem == newItem
        }
    }
) {

    var exoPlayer: ExoPlayer? = null

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.bind(currentList[holder.bindingAdapterPosition])
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.releasePlayer()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SingleTonAdapter.ViewHolder {
        return ViewHolder(
            ItemVideoTest02Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SingleTonAdapter.ViewHolder, position: Int) {
//        holder.bind(currentList[position])
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.releasePlayer()
        Glide.with(holder.itemBinding.root.context).clear(holder.itemBinding.ivThumbnail)
        super.onViewRecycled(holder)
    }

    inner class ViewHolder(val itemBinding: ItemVideoTest02Binding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        private lateinit var btnExoFullScreen: ImageView
        private lateinit var btnExoPlay: ImageButton
        private lateinit var btnExoPause: ImageView
        private lateinit var exoPlayerDim: View

        var seekTime = 0L
        var isPlay = false

        init {
            itemBinding.ivPlay.setOnClickListener {
                // 재생 눌렀을 때
                videoPlayStatus()
                playChange(true, currentList[bindingAdapterPosition])
            }

            itemBinding.ivPause.setOnClickListener {
                // 일시정지 눌렀을 때
                videoStopStatus()
                playChange(false, currentList[bindingAdapterPosition])
            }
        }

        fun bind(item: SingleTonVideo) {
            btnExoPlay =
                itemBinding.playerView.findViewById(com.google.android.exoplayer2.ui.R.id.exo_play)
            btnExoPause =
                itemBinding.playerView.findViewById(com.google.android.exoplayer2.ui.R.id.exo_pause)
            btnExoFullScreen = itemBinding.playerView.findViewById(R.id.exo_fullscreen_icon)
            exoPlayerDim = itemBinding.playerView.findViewById(R.id.bg_exo_dim)

            btnExoFullScreen.setOnClickListener {
                updateSeekTime(item)
                fullScreen.invoke(item)
            }

            Glide.with(itemBinding.root)
                .load(item.defaultThumbnail)
                .centerCrop()
                .into(itemBinding.ivThumbnail)

//            if (item.seekTime > 1000L) {
//                Glide.with(itemBinding.root)
//                    .load(updateVideoThumbnail(item))
//                    .centerCrop()
//                    .error(item.defaultThumbnail)
//                    .into(itemBinding.ivThumbnail)
//            }

//            if (item.playWhenReady) {
//                videoPlayStatus()
//            } else {
//                videoStopStatus()
//            }

            itemBinding.playerView.player = null

            if (item.playWhenReady == true) {
                // 영상 보여주기

                itemBinding.ivThumbnail.isVisible = false
                videoPlayStatus()

                ExoPlayerManager.instance.prepare(
                    itemBinding.root.context,
                    item.path,
                    true,
                    item.seekTime,
                    object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            super.onPlaybackStateChanged(playbackState)
                            when (playbackState) {
                                Player.STATE_READY -> {
                                    // playWhenReady == true 이면 미디어 재생이 시작
                                    // playWhenReady == false 이면 미디어 일시중지
                                    // 즉시 재생 불가능, 준비만 된 상태

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
                        }
                    },
                    itemBinding.playerView
                )

            } else {
                // 이미지 보여주기
                videoStopStatus()
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
            if (exoPlayer?.isPlaying == true) {
                Log.d(
                    "TAG",
                    "current id : ${currentList[bindingAdapterPosition].id} / pos: ${exoPlayer?.currentPosition}"
                )
                updateSeekTime(currentList[bindingAdapterPosition])
                itemBinding.playerView.postDelayed({ getCurrentPlayerPosition() }, 1000)
            }
        }

        private fun updateSeekTime(item: SingleTonVideo) {
            item.seekTime = exoPlayer?.currentPosition ?: 0L
        }

        private suspend fun updateVideoThumbnail(item: SingleTonVideo): Bitmap? {
            return withContext(Dispatchers.Default) {
                val mediaMetadataRetriever = MediaMetadataRetriever()
                mediaMetadataRetriever.setDataSource(item.path)

                val bitmap = try {
                    Log.e("bitmap", "VideoThumbnail / ${item.seekTime}")
                    mediaMetadataRetriever.getFrameAtTime(item.seekTime * 1000L)
                } catch (e: Exception) {
                    Log.e("bitmap", "error bitmap = / ".plus(e.stackTraceToString()))
                    null
                }
                item.videoThumbnail = bitmap
                bitmap
            }
        }

        private fun videoStopStatus() {
            itemBinding.ivThumbnail.isVisible = true
            itemBinding.ivPlay.isVisible = true
            itemBinding.ivPause.isVisible = false
        }

        private fun videoPlayStatus() {
            itemBinding.ivThumbnail.isVisible = false
            itemBinding.ivPlay.isVisible = false
            itemBinding.ivPause.isVisible = true
        }
    }
}
