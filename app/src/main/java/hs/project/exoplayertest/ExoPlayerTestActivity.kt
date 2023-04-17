package hs.project.exoplayertest

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import hs.project.exoplayertest.databinding.ActivityExoPlayerTestBinding

class ExoPlayerTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExoPlayerTestBinding

    private var exoPlayer: ExoPlayer? = null
    private lateinit var btnExoFullScreen: ImageView
    private lateinit var exoPlayerDim: View

    private var playWhenReady = false   // 재생 or 일시중지
    private var currentWindow = 0       // 현재 재생 위치
    private var playbackPosition = 0L   // 현재 위도우 지수

    private val test1 = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    private val test2 = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
    private val test3 = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"


    private val playbackStateListener: Player.Listener = playbackStateListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExoPlayerTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        btnExoFullScreen = binding.player.findViewById(R.id.exo_fullscreen_icon)
//        exoPlayerDim = binding.player.findViewById(R.id.bg_exo_dim)

        btnExoFullScreen.setOnClickListener {
            Toast.makeText(this, "전체화면으로 전환", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer() {
        if (exoPlayer == null) {

            /**
             * 가변 품질 스트리밍 : 네트워크 대역폭에 따라 스트림 품질이 변화
             *
             *  1. DASH = setMimeType(MimeTypes.APPLICATION_MPD)
             *  2. HLS = setMimeType(MimeTypes.APPLICATION_M3U8) : 라이브 스트리밍
             *  2. SmoothStreaming = setMimeType(MimeTypes.APPLICATION_M3U8)
             */
            val trackSelector = DefaultTrackSelector(this).apply {
                setParameters(buildUponParameters().setMaxVideoSizeSd())
            }

            exoPlayer = ExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .build()
                .also { exoPlayer ->
                    binding.player.player = exoPlayer

//                    val mediaItem = MediaItem.Builder()
//                        .setUri(getString(R.string.test_video_dash))
//                        .setMimeType(MimeTypes.APPLICATION_MPD)
//                        .build()

                    val mediaItem = MediaItem.fromUri(test1)
                    exoPlayer.setMediaItem(mediaItem)

                    /**
                     * 사용자가 중단한 부분부터 재생을 재개할 수 있습니다.
                     * 플레이어를 초기화할 때 이 상태 정보를 제공하기만 하면 됩니다.
                     */
                    exoPlayer.playWhenReady = playWhenReady
//                    exoPlayer.playWhenReady = false
                    exoPlayer.seekTo(currentWindow, playbackPosition)
                    exoPlayer.addListener(playbackStateListener)
                    exoPlayer.addAnalyticsListener(object : AnalyticsListener {
                        override fun onRenderedFirstFrame(
                            eventTime: AnalyticsListener.EventTime,
                            output: Any,
                            renderTimeMs: Long,
                        ) {
                            super.onRenderedFirstFrame(eventTime, output, renderTimeMs)
                            // 동영상의 첫번째 프레임이 렌더링 될 때 호출
                        }
                    })
                    exoPlayer.prepare()

                    /**
                     * 영상 재생목록 만드는 법
                     */
//                    val secondMediaItem = MediaItem.fromUri(test2)
//                    exoPlayer.addMediaItem(secondMediaItem)
                }
        }
    }

    private fun releasePlayer() {
        exoPlayer?.run {
            // 리스너 제거
            removeListener(playbackStateListener)
            playbackPosition = this.currentPosition
            currentWindow = this.currentMediaItemIndex
            playWhenReady = this.playWhenReady
            release()
        }
        exoPlayer = null
    }

    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                Player.STATE_READY -> {
                    // playWhenReady == true 이면 미디어 재생이 시작
                    // playWhenReady == false 이면 미디어 일시중지
                    // 즉시 재생 불가능, 준비만 된 상태

                    /**
                     * 이미지 터치 리스너 붙히기
                     */
                    binding.clPlay.setOnClickListener {
                        exoPlayer?.playWhenReady = true
                        binding.clPlay.visibility = View.INVISIBLE
                        binding.player.visibility = View.VISIBLE
                    }
                }
                Player.STATE_ENDED -> {
                    // 재생 완료
                }
                Player.STATE_BUFFERING ->{
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
                exoPlayerDim.visibility = View.VISIBLE
                btnExoFullScreen.visibility = View.VISIBLE
            } else {
                exoPlayerDim.visibility = View.GONE
                btnExoFullScreen.visibility = View.GONE
            }
        }
    }

    // 전체 화면 사용
    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}