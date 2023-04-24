package hs.project.exoplayertest.recylerview_2

import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import hs.project.exoplayertest.R
import hs.project.exoplayertest.databinding.DialogFullScreenBinding

class FullScreenDialog : DialogFragment() {

    companion object {
        const val TAG = "FullScreenDialog"
        const val VIDEO_ITEM = "video_item"
    }

    interface FullScreenCallback {
        fun onDismiss(item: TestVideo02)
    }

    private var callback: FullScreenCallback? = null

    private var _binding: DialogFullScreenBinding? = null
    private val binding get() = _binding!!

    private var videoItem: TestVideo02? = null

    private var seekTime = 0L

    private var isVolumeMute = true

    private lateinit var btnExoSmallScreen: ImageView
    private lateinit var btnVolume: ImageView

//    private lateinit var btnExoPlay: ImageButton
//    private lateinit var btnExoPause: ImageView
//    private lateinit var exoPlayerDim: View

    fun newInstance(item: TestVideo02): FullScreenDialog {
        val fullScreenDialog = FullScreenDialog()
        val args = Bundle()
        args.putParcelable(VIDEO_ITEM, item)
        fullScreenDialog.arguments = args
        return fullScreenDialog
    }

    fun setFullScreenCallback(fullScreenCallback: FullScreenCallback) {
        callback = fullScreenCallback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        arguments?.let {
            videoItem = it.getParcelable(VIDEO_ITEM)
            seekTime = videoItem?.seekTime ?: 0L
        }
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialog)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFullScreenBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideSystemUi()
        videoItem?.let { initExoPlayer(it) }
        btnVolume = binding.playerView.findViewById(R.id.iv_play_volume)
        btnExoSmallScreen = binding.playerView.findViewById(R.id.iv_small)

        btnVolume.setOnClickListener {
            if (isVolumeMute) {
                Toast.makeText(requireActivity(), "볼륨 사용", Toast.LENGTH_SHORT).show()
                btnVolume.setImageResource(R.drawable.icn_live_video_volume)
                binding.playerView.player?.volume = 0.7f
                binding.playerView.onResume()
                Log.e("121", "${binding.playerView.player?.volume}")
                isVolumeMute = false
            } else {
                Toast.makeText(requireActivity(), "볼륨 미사용", Toast.LENGTH_SHORT).show()
                btnVolume.setImageResource(R.drawable.icn_live_video_mute)
                binding.playerView.player?.volume = 0f
                binding.playerView.onResume()
                Log.e("121", "${binding.playerView.player?.volume}")
                isVolumeMute = true
            }
        }
        btnExoSmallScreen.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun initExoPlayer(item: TestVideo02) {

        ExoPlayer.Builder(requireActivity()).build()
            .also {
                binding.playerView.player = it
                it.setMediaItem(MediaItem.fromUri(Uri.parse(item.path)))
                it.playWhenReady = item.playWhenReady
                it.seekTo(item.seekTime)
                it.addListener(object : Player.Listener {

                    override fun onVolumeChanged(volume: Float) {
                        super.onVolumeChanged(volume)
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        when (playbackState) {
                            Player.STATE_READY -> {
                                Log.e("STATE_READY", "준비")
                            }

                            Player.STATE_ENDED -> {

                            }

                            Player.STATE_BUFFERING -> {

                            }

                            Player.STATE_IDLE -> {

                            }

                            else -> Unit
                        }
                    }

                })
                it.prepare()
            }
    }

    private fun hideSystemUi() {
        //                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
//                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

        dialog?.window?.let {
            WindowCompat.setDecorFitsSystemWindows(it, false)
            WindowInsetsControllerCompat(it, binding.root).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        videoItem?.let {
            it.seekTime = binding.playerView.player?.currentPosition ?: seekTime
            Log.e("time", "seekTime = ${it.seekTime}")
            callback?.onDismiss(it)
        }
    }
}
