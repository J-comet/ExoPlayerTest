package hs.project.exoplayertest.youtube

import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.MimeTypes
import hs.project.exoplayertest.databinding.ActivityYouTubeBinding
import java.util.Locale


class YouTubeActivity : AppCompatActivity() {

    private val binding by lazy { ActivityYouTubeBinding.inflate(layoutInflater) }

    private var YOUTUBE_VIDEO_ID = arrayOf("jZzBF49zWLw", "ZfpRZGJAo1o")
    var TAG = "테스트"
    private val BASE_URL = "https://www.youtube.com"
    private var mYoutubeLink: String? = null

    var player_now = 0
    var exo_controller: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        extractYoutubeUrl(YOUTUBE_VIDEO_ID[0], 0)

        Log.e("", "isPlaying = ${binding.playerView.player?.isPlaying}")
    }

    private fun extractYoutubeUrl(url: String, num: Int) {
        Log.i(TAG, " --------------------- 재생준비 ---------------------")
        mYoutubeLink = "$BASE_URL/watch?v=$url"
        player_now = num
        Log.i(TAG, " [유튜브주소변환] geturl= $url 들어온값: $num")

        val extractor: YouTubeExtractor = object : YouTubeExtractor(this) {
            override fun onExtractionComplete(
                ytFiles: SparseArray<YtFile>?,
                videoMeta: VideoMeta?
            ) {
                if (ytFiles != null) {
                    playVideo(ytFiles[22].url)
                    Log.i(
                        TAG,
                        " --------------------- sparseArray != null ---------------------"
                    )
                    Log.i(
                        TAG,
                        """${
                            """ extractYoutubeUrl() param videoMeta: ${videoMeta?.title} getUrl: ${ytFiles[22].url}"""
                        } SparseArraySize: ${ytFiles.size()} sparseArray(22).URl: ${ytFiles[22].url}"""
                    )
                }
            }
        }

        extractor.extract(mYoutubeLink, true, true)
    }

    private fun playVideo(downloadUrl: String) {

        ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                binding.playerView.player = exoPlayer

//                val mediaItem = MediaItem.fromUri(downloadUrl)
                val mediaItem = MediaItem.Builder()
                    .setUri(downloadUrl)
                    .setMimeType(
                        if (downloadUrl.uppercase(Locale.getDefault()).contains("M3U8")) {
                            MimeTypes.APPLICATION_M3U8
                        } else {
                            MimeTypes.APPLICATION_MPD
                        }
                    )
                    .build()
                exoPlayer.setMediaItem(mediaItem)

                exoPlayer.playWhenReady = true
                exoPlayer.prepare()
            }

    }
}