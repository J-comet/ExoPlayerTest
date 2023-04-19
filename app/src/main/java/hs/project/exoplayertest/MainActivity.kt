package hs.project.exoplayertest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import hs.project.exoplayertest.custom_recycler.CustomRecyclerActivity
import hs.project.exoplayertest.databinding.ActivityMainBinding
import hs.project.exoplayertest.recyclerview.RecyclerActivity

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private var player: ExoPlayer? = null

    private var playWhenReady = false
    private var playbackPosition = 0L

    private var playWhenReady2 = false
    private var playbackPosition2 = 0L

    private var playWhenReady3 = false
    private var playbackPosition3 = 0L

    private val test1 = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    private val test2 = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
    private val test3 = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnRecyclerview.setOnClickListener {
            startActivity(Intent(this, RecyclerActivity::class.java))
        }
        binding.btnCustomRecyclerview.setOnClickListener {
            startActivity(Intent(this, CustomRecyclerActivity::class.java))
        }

//        player = ExoPlayer.Builder(this).build()

        binding.videoView.player = ExoPlayer.Builder(this).build().also {
            val mediaItem = MediaItem.fromUri(test1)
            it.setMediaItem(mediaItem)
            it.playWhenReady = false
            it.seekTo(playbackPosition)
            it.prepare()
            it.addListener(object : Player.Listener {
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

                    if (isPlaying) {
                        binding.videoView2.player?.playWhenReady = false
                        binding.videoView3.player?.playWhenReady = false
//                        binding.videoView2.player?.stop()
//                        binding.videoView3.player?.stop()
                    } else {
                        playWhenReady = it.playWhenReady
                        playbackPosition = it.currentPosition
                    }
                    Log.e("3", "111 ".plus("playWhenReady/").plus(playWhenReady).plus(" playbackPosition/").plus(playbackPosition))
                }
            })
        }
        binding.videoView2.player = ExoPlayer.Builder(this).build().also {
            val mediaItem = MediaItem.fromUri(test2)
            it.setMediaItem(mediaItem)
            it.playWhenReady = false
            it.seekTo(playbackPosition)
            it.prepare()
            it.addListener(object : Player.Listener {
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
                    if (isPlaying) {
                        binding.videoView.player?.playWhenReady = false
                        binding.videoView3.player?.playWhenReady = false
//                        binding.videoView.player?.stop()
//                        binding.videoView3.player?.stop()
                    } else {
                        playWhenReady2 = it.playWhenReady
                        playbackPosition2 = it.currentPosition
                    }
                    Log.e("3", "222 ".plus("playWhenReady2/").plus(playWhenReady2).plus(" playbackPosition2/").plus(playbackPosition2))
                }
            })
        }
        binding.videoView3.player = ExoPlayer.Builder(this).build().also {
            val mediaItem = MediaItem.fromUri(test3)
            it.setMediaItem(mediaItem)
            it.playWhenReady = false
            it.seekTo(playbackPosition)
            it.prepare()
            it.addListener(object : Player.Listener {
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
                    if (isPlaying) {
                        binding.videoView.player?.playWhenReady = false
                        binding.videoView2.player?.playWhenReady = false
//                        binding.videoView.player?.stop()
//                        binding.videoView2.player?.stop()
                    } else {
                        playWhenReady3 = it.playWhenReady
                        playbackPosition3 = it.currentPosition
                    }
                    Log.e("3", "333 ".plus("playWhenReady3/").plus(playWhenReady3).plus(" playbackPosition3/").plus(playbackPosition3))
                }
            })
        }
    }


    override fun onStart() {
        super.onStart()
//        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
//        releasePlayer()
    }

    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
//            currentWindow = this.currentMediaItemIndex
            playWhenReady = this.playWhenReady
            release()
        }
        player = null
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                binding.videoView.player = exoPlayer
                val mediaItem = MediaItem.fromUri(getString(R.string.media_url_mp4))
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(playbackPosition)
                exoPlayer.prepare()
            }
    }
}