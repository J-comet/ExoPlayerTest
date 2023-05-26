package hs.project.exoplayertest.util

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.util.MimeTypes
import java.util.Locale


object ExoPlayerUtil {

    const val VOLUME_MUTE = 0f // 음소거
    const val PLAY = "play"
    const val PAUSE = "pause"
    const val STOP = "stop"

    var currentVideoNo = 0
    var currentVideoDetailNo = 0
    var currentPlayingWindow = 0
    var currentPlaybackPosition = 0L

    var exoPlayer: ExoPlayer? = null
    private var playbackStateListener: Player.Listener? = null

    private const val DEFAULT_MAX_INITIAL_BITRATE = Int.MAX_VALUE.toLong()

    fun prepare(
        context: Context,
        videoPath: String,
        playWhenReady: Boolean,
        seekTime: Long,
        volume: Float,
        playerListener: Player.Listener,
        playerView: PlayerView,
    ): ExoPlayer? {

        val defaultBandwidthMeter = DefaultBandwidthMeter.Builder(context)
            .setInitialBitrateEstimate(DEFAULT_MAX_INITIAL_BITRATE)
            .build()

        exoPlayer = ExoPlayer.Builder(context)
            .setTrackSelector(DefaultTrackSelector(context))
            .setBandwidthMeter(defaultBandwidthMeter)
            .build()

        exoPlayer?.also {
            playerView.player = it
            this.playbackStateListener = playerListener
//            it.setMediaItem(MediaItem.fromUri(Uri.parse(videoPath)))
            val mediaItem = MediaItem.Builder()
                .setUri(videoPath)
                .setMimeType(
                    if (videoPath.uppercase(Locale.getDefault()).contains("M3U8")) {
                        MimeTypes.APPLICATION_M3U8
                    } else {
                        MimeTypes.APPLICATION_MP4
                    }
                )
                .build()
            it.setMediaItem(mediaItem)
            it.playWhenReady = playWhenReady
            it.seekTo(seekTime)
            it.volume = volume
            it.addListener(playerListener)
            it.prepare()
        }
        return exoPlayer
    }

    fun playbackStateListener(
        ready: () -> Unit,
        end: () -> Unit,
        buffering: () -> Unit,
        idle: () -> Unit,
        isPlayingChanged: (isPlaying: Boolean) -> Unit,
    ) = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                Player.STATE_READY -> {
                    // playWhenReady == true 이면 미디어 재생이 시작
                    // playWhenReady == false 이면 미디어 일시중지
                    // 즉시 재생 불가능, 준비만 된 상태
                    ready()
                }

                Player.STATE_ENDED -> {
                    // 재생 완료
                    end()
                }

                Player.STATE_BUFFERING -> {
                    buffering()
                }

                Player.STATE_IDLE -> {
                    idle()
                }

                else -> {
//                    playerView.hideController()
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            // 실제로 미디어 재생하는지 여부를 알 수 있음
            isPlayingChanged(isPlaying)
        }
    }

    fun ready(
        vol: Float,
        stateListener: Player.Listener,
        currentWindow: Int,
        playbackPosition: Long,
    ) {

        exoPlayer?.apply {

            if (playbackStateListener != null) {
                removeListener(playbackStateListener!!)
            }

//            repeatMode = ExoPlayer.REPEAT_MODE_ONE
            playWhenReady = true
            volume = vol
            seekTo(
                currentWindow,
                playbackPosition
            ) // 초기화 할때 currentWindow, playbackPosition 이용해서 중단한 곳부터 재생 재개
            prepare()
            addListener(stateListener)
        }
        this.playbackStateListener = stateListener
    }

    fun play() {
        exoPlayer?.playWhenReady = true
        exoPlayer?.play()
    }

    fun pause() {
        exoPlayer?.playWhenReady = false
    }

    fun releasePlayer() {
        exoPlayer?.run {
            // 리스너 제거
            playbackStateListener?.let { removeListener(it) }
//            playbackPosition = this.currentPosition
//            currentWindow = this.currentMediaItemIndex
            playWhenReady = this.playWhenReady
            release()
        }
        exoPlayer = null
    }

//    fun init(
//        context: Context,
//        videoPath: String,
//        playerView: PlayerView,
//        playWhenReady: Boolean,
//        currentWindow: Int,
//        playbackPosition: Long,
//        volume: Float,
//        stateListener: Player.Listener
//    ) {
//
//        /**
//         * 가변 품질 스트리밍 : 네트워크 대역폭에 따라 스트림 품질이 변화
//         *
//         *  1. DASH = setMimeType(MimeTypes.APPLICATION_MPD)
//         *  2. HLS = setMimeType(MimeTypes.APPLICATION_M3U8) : 라이브 스트리밍
//         *  2. SmoothStreaming = setMimeType(MimeTypes.APPLICATION_M3U8)
//         */
//        val trackSelector = DefaultTrackSelector(context).apply {
//            setParameters(buildUponParameters().setMaxVideoSizeSd())
//        }
//
//        playbackStateListener = stateListener
//
//        exoPlayer = ExoPlayer.Builder(context)
//            .setTrackSelector(trackSelector)
//            .build()
//            .also { exoPlayer ->
////                playerView.player = exoPlayer
//
////                    val mediaItem = MediaItem.Builder()
////                        .setUri(getString(R.string.test_video_dash))
////                        .setMimeType(MimeTypes.APPLICATION_MPD)
////                        .build()
//
//                val mediaItem = MediaItem.fromUri(videoPath)
//                exoPlayer.setMediaItem(mediaItem)
//                exoPlayer.playWhenReady = playWhenReady
//                exoPlayer.volume = volume
//
//                // 초기화 할때 currentWindow, playbackPosition 이용해서 중단한 곳부터 재생 재개
//                exoPlayer.seekTo(currentWindow, playbackPosition)
//                exoPlayer.addListener(stateListener)
//
//                exoPlayer.addAnalyticsListener(object : AnalyticsListener {
//                    override fun onRenderedFirstFrame(
//                        eventTime: AnalyticsListener.EventTime,
//                        output: Any,
//                        renderTimeMs: Long,
//                    ) {
//                        super.onRenderedFirstFrame(eventTime, output, renderTimeMs)
//                        // 동영상의 첫번째 프레임이 렌더링 될 때 호출
//                        Log.instance.e("Player 첫번째 렌더링 $eventTime")
//                        Log.instance.e("Player 첫번째 렌더링 $output")
//                        Log.instance.e("Player 첫번째 렌더링 $renderTimeMs")
//                    }
//                })
//                exoPlayer.prepare()
//            }
//    }


}