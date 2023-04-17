package hs.project.exoplayertest.lifecycle

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import hs.project.exoplayertest.R

class LifecycleAdapter(private val videoUrls: List<String>, private val lifecycleOwner: LifecycleOwner) :
    RecyclerView.Adapter<LifecycleAdapter.VideoViewHolder>() {

    private var player: ExoPlayer? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val playerView = LayoutInflater.from(parent.context).inflate(R.layout.item_player_view, parent, false)
        return VideoViewHolder(playerView)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videoUrls[position])
    }

    override fun getItemCount() = videoUrls.size

    override fun onViewDetachedFromWindow(holder: VideoViewHolder) {
        holder.releasePlayer()
        super.onViewDetachedFromWindow(holder)
    }

    inner class VideoViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(videoUrl: String) {
            releasePlayer()
            val playerView = view.findViewById<PlayerView>(R.id.player)

            val player = ExoPlayer.Builder(playerView.context).build()
            playerView.player = player

            val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
            player.setMediaItem(mediaItem)

//            val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(playerView.context, "exoplayer-codelab")
//            val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
//                .createMediaSource(MediaItem.fromUri(Uri.parse(videoUrl)))

//            player.setMediaSource(mediaSource)
            player.prepare()

            player.playWhenReady = true
            player.repeatMode = Player.REPEAT_MODE_ALL

            player.let {
                it.playWhenReady = true
                it.seekTo(0L)
            }

            playerView.setShowNextButton(false)
            playerView.setShowPreviousButton(false)

            playerView.onResume()
            lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onPause(owner: LifecycleOwner) {
                    player.playWhenReady = false
                }

                override fun onResume(owner: LifecycleOwner) {
                    player.playWhenReady = true
                }

                override fun onDestroy(owner: LifecycleOwner) {
                    releasePlayer()
                }
            })
        }

        fun releasePlayer() {
            player?.let {
                it.release()
                player = null
            }
        }
    }
}
