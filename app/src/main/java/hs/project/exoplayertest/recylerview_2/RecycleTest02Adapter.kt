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
        Log.e("onViewAttachedToWindow", "AttachedToWindow")
        lifecycleCoroutineScope.launch {
            holder.bind(currentList[holder.bindingAdapterPosition])
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        holder.releasePlayer()
        super.onViewDetachedFromWindow(holder)
        Log.e("onViewDetachedFromWindow", "DetachedFromWindow")
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

            itemBinding.playerView.player = null

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
                .load(item.videoThumbnail ?: item.defaultThumbnail)
                .centerCrop()
                .into(itemBinding.ivThumbnail)

            if (item.seekTime > 1000L) {
                Glide.with(itemBinding.root)
                    .load(updateVideoThumbnail(item))
                    .centerCrop()
                    .error(item.defaultThumbnail)
                    .into(itemBinding.ivThumbnail)
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
                            if (item.playWhenReady) {
                                exoPlayer?.prepare()
                                exoPlayer?.play()
                                Log.e("tag", "11111111 Play / " + event.name)
                            }
                        }
                        Lifecycle.Event.ON_PAUSE -> {
                            exoPlayer?.pause()
                            Log.e("tag", "2222222 / " + event.name)
                        }
                        else -> Unit
                    }
                }
            })

            if (item.playWhenReady) {
                videoPlayStatus()
                initExoPlayer(item)
            } else {
                videoStopStatus()
            }
        }

        fun releasePlayer() {
            exoPlayer?.release()
            exoPlayer = null
        }

        // 폴딩 방식으로
        private fun getCurrentPlayerPosition(item: TestVideo02) {
            if (exoPlayer?.isPlaying == true) {
                Log.d(
                    "TAG",
                    "current id : ${item.id} / pos: ${exoPlayer?.currentPosition}"
                )
                updateSeekTime(item)
                itemBinding.playerView.postDelayed({ getCurrentPlayerPosition(item) }, 1000)
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
                    mediaMetadataRetriever.getFrameAtTime(item.seekTime * 1000L)
                } catch (e: Exception) {
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

        private fun initExoPlayer(item: TestVideo02) {
            exoPlayer = ExoPlayer.Builder(itemBinding.root.context).build()
                .also {
                    itemBinding.playerView.player = it
                    it.setMediaItem(MediaItem.fromUri(Uri.parse(item.path)))
                    it.playWhenReady = item.playWhenReady
                    it.seekTo(item.seekTime)
                    it.addListener(object : Player.Listener {
                        override fun onRenderedFirstFrame() {
                            super.onRenderedFirstFrame()
//                            if (item.playWhenReady) {
//                                videoPlayStatus()
//                            }
                        }

                        override fun onPlaybackStateChanged(playbackState: Int) {
                            super.onPlaybackStateChanged(playbackState)
                            when (playbackState) {
                                Player.STATE_READY -> {
                                    Log.e("STATE_READY", "준비")
                                    itemBinding.progress.isVisible = false
                                    if (item.playWhenReady) {
                                        itemBinding.ivPause.isVisible = true
                                    } else {
                                        itemBinding.ivPlay.isVisible = true
                                    }
                                }

                                Player.STATE_ENDED -> {
//                                    item.seekTime = exoPlayer?.currentPosition ?: 0L
                                }

                                Player.STATE_BUFFERING -> {
                                    itemBinding.progress.isVisible = true
                                    itemBinding.ivPause.isVisible = false
                                    itemBinding.ivPlay.isVisible = false
                                }

                                Player.STATE_IDLE -> {
//                                    item.seekTime = exoPlayer?.currentPosition ?: 0L
                                }

                                else -> Unit
                            }
                        }

                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            super.onIsPlayingChanged(isPlaying)
                            getCurrentPlayerPosition(item)
                            updateSeekTime(item)
                        }
                    })
                    it.prepare()
                }
        }
    }
}
