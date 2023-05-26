package hs.project.exoplayertest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.exoplayer2.ExoPlayer
import hs.project.exoplayertest.databinding.ActivityTestVideoBinding
import hs.project.exoplayertest.util.ExoPlayerUtil

class TestVideoActivity : AppCompatActivity() {

    private val binding by lazy { ActivityTestVideoBinding.inflate(layoutInflater) }

    var exoPlayer: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val videoPath = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"

        initExoPlayer(videoPath)
    }

    private fun initExoPlayer(videoPath: String) {
        exoPlayer = ExoPlayerUtil.prepare(
            this,
            videoPath,
            true,
            0,
            1f,
            ExoPlayerUtil.playbackStateListener(
                ready = {

                },
                end = {
                    exoPlayer?.seekTo(0)
                    exoPlayer?.playWhenReady = false
                },
                buffering = {

                },
                idle = {},
                isPlayingChanged = { isPlay ->

                }
            ),
            binding.playerView
        )
    }
}