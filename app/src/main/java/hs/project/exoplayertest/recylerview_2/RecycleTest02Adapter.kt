package hs.project.exoplayertest.recylerview_2

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import hs.project.exoplayertest.R
import hs.project.exoplayertest.databinding.ItemVideoTest02Binding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecycleTest02Adapter(
    private val lifecycleCoroutineScope: LifecycleCoroutineScope,
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

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        lifecycleCoroutineScope.launch {
            holder.bind(currentList[holder.bindingAdapterPosition])
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        holder.releasePlayer()
        super.onViewDetachedFromWindow(holder)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecycleTest02Adapter.ViewHolder {
        return ViewHolder(
            ItemVideoTest02Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecycleTest02Adapter.ViewHolder, position: Int) {
//        holder.bind(currentList[position])
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.releasePlayer()
        Glide.with(holder.itemBinding.root.context).clear(holder.itemBinding.ivThumbnail)
        super.onViewRecycled(holder)
    }

    inner class ViewHolder(val itemBinding: ItemVideoTest02Binding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        private var lifecycleOwner: LifecycleOwner? = null

        var exoPlayer: ExoPlayer? = null

        private lateinit var btnExoFullScreen: ImageView
        private lateinit var btnExoPlay: ImageButton
        private lateinit var btnExoPause: ImageView
        private lateinit var exoPlayerDim: View

        init {
            itemView.doOnAttach {
                lifecycleOwner = itemView.findViewTreeLifecycleOwner()
            }
            itemView.doOnDetach {
                lifecycleOwner = null
            }
        }

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

        suspend fun bind(item: TestVideo02) {
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

            /**
             * 이전에 재생된 비디오가 무엇인지 알고 그 아이템만 썸네일 업데이트 하도록 추가 작업 필요
             *
             * 현재 코드는 1초 이상 지난 모든 영상 썸네일 새로 따는 중
             */

            lifecycleOwner?.lifecycle?.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    when(event) {
                        Lifecycle.Event.ON_START -> {
                            exoPlayer?.prepare()
                            exoPlayer?.play()
                            Log.e("tag", "11111111 / " + event.name)
                        }
                        Lifecycle.Event.ON_PAUSE -> {
                            exoPlayer?.pause()
                            Log.e("tag", "2222222 / " + event.name)
                        }
                        else -> Unit
                    }
                }
            })

            if (item.seekTime > 1000L) {
                Glide.with(itemBinding.root)
                    .load(updateVideoThumbnail(item))
                    .centerCrop()
                    .error(item.defaultThumbnail)
                    .into(itemBinding.ivThumbnail)
            } else {
                Glide.with(itemBinding.root)
                    .load(item.defaultThumbnail)
                    .centerCrop()
                    .into(itemBinding.ivThumbnail)
            }


//            if (item.videoThumbnail == null) {
//                Log.e("1", "videoThumbnail = null")
//                Glide.with(itemBinding.root)
//                    .load(item.defaultThumbnail)
//                    .centerCrop()
//                    .into(itemBinding.ivThumbnail)
//            } else {
//                Log.e("1", "videoThumbnail = GOOD")
//                Glide.with(itemBinding.root)
//                    .load(item.videoThumbnail)
//                    .centerCrop()
//                    .error(item.defaultThumbnail)
//                    .into(itemBinding.ivThumbnail)
//            }

            if (item.playWhenReady) {
                videoPlayStatus()
            } else {
                videoStopStatus()
            }

            exoPlayer = ExoPlayer.Builder(itemBinding.root.context).build()
                .also {
                    itemBinding.playerView.player = it
                    it.setMediaItem(MediaItem.fromUri(Uri.parse(item.path)))
                    it.playWhenReady = true
                    it.seekTo(item.seekTime)
                    it.addListener(object : Player.Listener {
                        override fun onRenderedFirstFrame() {
                            super.onRenderedFirstFrame()
                            if (item.playWhenReady) {
                                videoPlayStatus()
                            }
                        }

                        override fun onPlaybackStateChanged(playbackState: Int) {
                            super.onPlaybackStateChanged(playbackState)
                            when (playbackState) {
                                Player.STATE_READY -> {
                                    // playWhenReady == true 이면 미디어 재생이 시작
                                    // playWhenReady == false 이면 미디어 일시중지
                                    // 즉시 재생 불가능, 준비만 된 상태
                                    Log.e("STATE_READY", "준비")

//                                    if (item.playWhenReady) {
//                                        videoPlayStatus()
//                                    } else {
//                                        videoStopStatus()
//                                    }

                                    if (item.playWhenReady) {
                                        exoPlayer?.play()
                                    } else {
                                        exoPlayer?.pause()
                                    }
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

                            getCurrentPlayerPosition()
                            updateSeekTime(item)
//                            playChange.invoke(isPlaying, item)
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
            if (exoPlayer?.isPlaying == true) {
                Log.d(
                    "TAG",
                    "current id : ${currentList[bindingAdapterPosition].id} / pos: ${exoPlayer?.currentPosition}"
                )
                updateSeekTime(currentList[bindingAdapterPosition])
                itemBinding.playerView.postDelayed({ getCurrentPlayerPosition() }, 1000)
            }
        }

        private fun updateSeekTime(item: TestVideo02) {
            item.seekTime = exoPlayer?.currentPosition ?: 0L
        }

        private suspend fun updateVideoThumbnail(item: TestVideo02): Bitmap? {
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
