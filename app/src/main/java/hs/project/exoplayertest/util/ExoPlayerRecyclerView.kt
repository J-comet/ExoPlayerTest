package hs.project.exoplayertest.util

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.exoplayer.model.MediaObject
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import hs.project.exoplayertest.custom_recycler.PlayerViewHolder
import java.util.Objects

class ExoPlayerRecyclerView : RecyclerView {
    /**
     * PlayerViewHolder UI component
     * Watch PlayerViewHolder class
     */
    private var mediaCoverImage: ImageView? = null
    private var volumeControl: ImageView? = null
    private var progressBar: ProgressBar? = null
    private var viewHolderParent: View? = null
    private var mediaContainer: FrameLayout? = null
    private var videoSurfaceView: PlayerView? = null
    private var videoPlayer: ExoPlayer? = null

    /**
     * variable declaration
     */
    // Media List
    private var mediaObjects = ArrayList<MediaObject>()
    private var videoSurfaceDefaultHeight = 0
    private var screenDefaultHeight = 0
    private var myContext: Context? = null
    private var playPosition = -1
    private var isVideoViewAdded = false
//    private var requestManager: RequestManager? = null

    // controlling volume state
    private var volumeState: VolumeState? = null
    private val videoViewClickListener = OnClickListener { toggleVolume() }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        this.myContext = context.applicationContext
        val display = (Objects.requireNonNull(
            getContext().getSystemService(Context.WINDOW_SERVICE)
        ) as WindowManager).defaultDisplay
        val point = Point()
        display.getSize(point)
        videoSurfaceDefaultHeight = point.x
        screenDefaultHeight = point.y
        videoSurfaceView = PlayerView(this.context)
        videoSurfaceView!!.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
//        val videoTrackSelectionFactory: TrackSelection.Factory =
//            AdaptiveTrackSelection.Factory(bandwidthMeter)
//        val trackSelector: TrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)

        //Create the player using ExoPlayerFactory
        videoPlayer = ExoPlayer.Builder(context).build()

        // Disable Player Control
        videoSurfaceView!!.useController = false
        // Bind the player to the view.
        videoSurfaceView!!.player = videoPlayer
        // Turn on Volume
        setVolumeControl(VolumeState.ON)
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    if (mediaCoverImage != null) {
                        // show the old thumbnail
                        mediaCoverImage!!.visibility = VISIBLE
                    }

                    // There's a special case when the end of the list has been reached.
                    // Need to handle that with this bit of logic
                    if (!recyclerView.canScrollVertically(1)) {
                        playVideo(true)
                    } else {
                        playVideo(false)
                    }
                }
            }

        })
        addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {}
            override fun onChildViewDetachedFromWindow(view: View) {
                if (viewHolderParent != null && viewHolderParent == view) {
                    resetVideoView()
                }
            }
        })

        videoPlayer?.addListener(
            object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    when (playbackState) {
                        Player.STATE_READY -> {
                            // playWhenReady == true 이면 미디어 재생이 시작
                            // playWhenReady == false 이면 미디어 일시중지
                            // 즉시 재생 불가능, 준비만 된 상태

                            if (progressBar != null) {
                                progressBar!!.visibility = GONE
                            }
                            if (!isVideoViewAdded) {
                                addVideoView()
                            }
                        }

                        Player.STATE_ENDED -> {
                            // 재생 완료
                            videoPlayer!!.seekTo(0)
                        }

                        Player.STATE_BUFFERING -> {
                            if (progressBar != null) {
                                progressBar!!.visibility = VISIBLE
                            }
                        }

                        Player.STATE_IDLE -> {
                        }

                        else -> {
//                                    playerView.hideController()
                        }
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    // 실제로 미디어 재생하는지 여부를 알 수 있음
                    if (isPlaying) {
//                        exoPlayerDim.visibility = View.VISIBLE
//                        btnExoFullScreen.visibility = View.VISIBLE
                    } else {
//                        exoPlayerDim.visibility = View.GONE
//                        btnExoFullScreen.visibility = View.GONE
                    }
                }
            }
        )

    }

    fun playVideo(isEndOfList: Boolean) {
        val targetPosition: Int
        if (!isEndOfList) {
            val startPosition =
                (layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
            var endPosition =
                (layoutManager as LinearLayoutManager?)!!.findLastVisibleItemPosition()

            // if there is more than 2 list-items on the screen, set the difference to be 1
            if (endPosition - startPosition > 1) {
                endPosition = startPosition + 1
            }

            // something is wrong. return.
            if (startPosition < 0 || endPosition < 0) {
                return
            }

            // if there is more than 1 list-item on the screen
            targetPosition = if (startPosition != endPosition) {
                val startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition)
                val endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition)
                if (startPositionVideoHeight > endPositionVideoHeight) startPosition else endPosition
            } else {
                startPosition
            }
        } else {
            targetPosition = mediaObjects.size - 1
        }
        Log.d(TAG, "playVideo: target position: $targetPosition")

        // video is already playing so return
        if (targetPosition == playPosition) {
            return
        }

        // set the position of the list-item that is to be played
        playPosition = targetPosition
        if (videoSurfaceView == null) {
            return
        }

        // remove any old surface views from previously playing videos
        videoSurfaceView!!.visibility = INVISIBLE
        removeVideoView(videoSurfaceView)
        val currentPosition = targetPosition - (Objects.requireNonNull(
            layoutManager
        ) as LinearLayoutManager).findFirstVisibleItemPosition()
        val child = getChildAt(currentPosition) ?: return
        val holder = child.tag as PlayerViewHolder
        if (holder == null) {
            playPosition = -1
            return
        }
        mediaCoverImage = holder.mediaCoverImage
        progressBar = holder.progressBar
        volumeControl = holder.volumeControl
        viewHolderParent = holder.itemView
//        requestManager = holder.requestManager
        mediaContainer = holder.mediaContainer
        videoSurfaceView!!.player = videoPlayer
        viewHolderParent!!.setOnClickListener(videoViewClickListener)

        val mediaUrl = mediaObjects[targetPosition].url
        if (mediaUrl != null) {
            val mediaItem = MediaItem.fromUri(mediaUrl)
            videoPlayer?.setMediaItem(mediaItem)
            videoPlayer?.prepare()
            videoPlayer?.playWhenReady = true
        }
    }

    /**
     * Returns the visible region of the video surface on the screen.
     * if some is cut off, it will return less than the @videoSurfaceDefaultHeight
     */
    private fun getVisibleVideoSurfaceHeight(playPosition: Int): Int {
        val at = playPosition - (Objects.requireNonNull(
            layoutManager
        ) as LinearLayoutManager).findFirstVisibleItemPosition()
        Log.d(TAG, "getVisibleVideoSurfaceHeight: at: $at")
        val child = getChildAt(at) ?: return 0
        val location = IntArray(2)
        child.getLocationInWindow(location)
        return if (location[1] < 0) {
            location[1] + videoSurfaceDefaultHeight
        } else {
            screenDefaultHeight - location[1]
        }
    }

    // Remove the old player
    private fun removeVideoView(videoView: PlayerView?) {
        val parent = videoView!!.parent as? ViewGroup ?: return
        val index = parent.indexOfChild(videoView)
        if (index >= 0) {
            parent.removeViewAt(index)
            isVideoViewAdded = false
            viewHolderParent!!.setOnClickListener(null)
        }
    }

    private fun addVideoView() {
        mediaContainer!!.addView(videoSurfaceView)
        isVideoViewAdded = true
        videoSurfaceView!!.requestFocus()
        videoSurfaceView!!.visibility = VISIBLE
        videoSurfaceView!!.alpha = 1f
        mediaCoverImage!!.visibility = GONE
    }

    private fun resetVideoView() {
        if (isVideoViewAdded) {
            removeVideoView(videoSurfaceView)
            playPosition = -1
            videoSurfaceView!!.visibility = INVISIBLE
            mediaCoverImage!!.visibility = VISIBLE
        }
    }

    fun releasePlayer() {
        if (videoPlayer != null) {
            videoPlayer!!.release()
            videoPlayer = null
        }
        viewHolderParent = null
    }

    fun onPausePlayer() {
        if (videoPlayer != null) {
            videoPlayer!!.stop(true)
        }
    }

    private fun toggleVolume() {
        if (videoPlayer != null) {
            if (volumeState == VolumeState.OFF) {
                Log.d(TAG, "togglePlaybackState: enabling volume.")
                setVolumeControl(VolumeState.ON)
            } else if (volumeState == VolumeState.ON) {
                Log.d(TAG, "togglePlaybackState: disabling volume.")
                setVolumeControl(VolumeState.OFF)
            }
        }
    }

    //public void onRestartPlayer() {
    //  if (videoPlayer != null) {
    //   playVideo(true);
    //  }
    //}
    private fun setVolumeControl(state: VolumeState) {
        volumeState = state
        if (state == VolumeState.OFF) {
            videoPlayer!!.volume = 0f
            animateVolumeControl()
        } else if (state == VolumeState.ON) {
            videoPlayer!!.volume = 1f
            animateVolumeControl()
        }
    }

    private fun animateVolumeControl() {
        if (volumeControl != null) {
            volumeControl!!.bringToFront()
            if (volumeState == VolumeState.OFF) {
//                requestManager!!.load(R.drawable.ic_volume_off)
//                    .into(volumeControl!!)
            } else if (volumeState == VolumeState.ON) {
//                requestManager!!.load(R.drawable.ic_volume_on)
//                    .into(volumeControl!!)
            }
            volumeControl!!.animate().cancel()
            volumeControl!!.alpha = 1f
            volumeControl!!.animate()
                .alpha(0f)
                .setDuration(600).startDelay = 1000
        }
    }

    fun setMediaObjects(mediaObjects: ArrayList<MediaObject>) {
        this.mediaObjects = mediaObjects
    }

    /**
     * Volume ENUM
     */
    private enum class VolumeState {
        ON, OFF
    }

    companion object {
        private const val TAG = "ExoPlayerRecyclerView"
        private const val AppName = "Android ExoPlayer"
    }
}