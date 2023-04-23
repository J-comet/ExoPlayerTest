package hs.project.exoplayertest.util

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView

class ExoPlayerManager {

    private var exoPlayer: ExoPlayer? = null
    private var playbackPosition = 0L
    private var playWhenReady = false
    private var playerListener: Player.Listener? = null

    companion object {
        var instance: ExoPlayerManager = ExoPlayerManager()
    }

    fun prepare(
        context: Context,
        videoPath: String,
        playWhenReady: Boolean,
        playbackPosition: Long,
        playerListener: Player.Listener,
        playerView: PlayerView
    ) {

        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build()
        }

        exoPlayer?.also {
            playerView.player = it
            this.playerListener = playerListener
            it.setMediaItem(MediaItem.fromUri(Uri.parse(videoPath)))
            it.playWhenReady = playWhenReady
            it.seekTo(playbackPosition)
            it.addListener(playerListener)
            it.prepare()
        }
    }

    fun release() {
        exoPlayer?.let { exoPlayer ->

            Log.e("release", "release / ${exoPlayer.currentPosition}")
            Log.e("release", "release / ${exoPlayer.currentMediaItemIndex}")
            Log.e("release", "release / ${exoPlayer.playWhenReady}")

            playbackPosition = exoPlayer.currentPosition
            playWhenReady = exoPlayer.playWhenReady
            playerListener?.let { exoPlayer.removeListener(it) }
            exoPlayer.release()
        }
        exoPlayer = null
    }

    fun getExoPlayer(): ExoPlayer? {
        return this.exoPlayer
    }
}