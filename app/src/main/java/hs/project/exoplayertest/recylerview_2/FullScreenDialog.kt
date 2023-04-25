package hs.project.exoplayertest.recylerview_2

import android.content.Context
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
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
        fun onDismiss()
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

    private var audioManager: AudioManager? = null

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
//        activity?.volumeControlStream = AudioManager.STREAM_MUSIC

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

        audioManager = activity?.getSystemService(Context.AUDIO_SERVICE) as AudioManager?

        dialog?.setOnKeyListener { dialog, keyCode, event ->
            when(keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    audioManager?.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_SHOW_UI
                    )

                    if ((audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0) > 0) {
                        btnVolume.setImageResource(R.drawable.icn_live_video_volume)
                    }

                    return@setOnKeyListener true
                }
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    audioManager?.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_SHOW_UI
                    )

                    if ((audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0) < 1) {
                        btnVolume.setImageResource(R.drawable.icn_live_video_mute)
                    }

                    return@setOnKeyListener true
                }
                else -> return@setOnKeyListener false
            }
        }

        hideSystemUi()

        btnVolume = binding.playerView.findViewById(R.id.iv_play_volume)
        btnExoSmallScreen = binding.playerView.findViewById(R.id.iv_small)

        btnVolume.setOnClickListener {
            if (isVolumeMute) {
                btnVolume.setImageResource(R.drawable.icn_live_video_volume)
                audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, 10, 0)
                binding.playerView.player?.volume = 1f
                binding.playerView.onResume()
                isVolumeMute = false
            } else {
                btnVolume.setImageResource(R.drawable.icn_live_video_mute)
                audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
                binding.playerView.player?.volume = 0f
                binding.playerView.onResume()
                isVolumeMute = true
            }
        }
        btnExoSmallScreen.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    override fun onResume() {
        super.onResume()
        videoItem?.let { initExoPlayer(it) }
    }

    override fun onPause() {
        super.onPause()
        binding.playerView.player?.release()
    }

    private fun initExoPlayer(item: TestVideo02) {

        ExoPlayer.Builder(requireActivity()).build()
            .also {
                binding.playerView.player = it
                it.setMediaItem(MediaItem.fromUri(Uri.parse(item.path)))
                it.playWhenReady = item.playWhenReady
                it.seekTo(item.seekTime)
                it.volume = 0f
                it.addListener(object : Player.Listener {

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        when (playbackState) {
                            Player.STATE_READY -> {
                                Log.e("STATE_READY", "준비")
                            }

                            Player.STATE_ENDED -> {
                                Log.e("STATE_ENDED", "영상 종료")
                                it.seekTo(0)
                                it.playWhenReady = false
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
        callback?.onDismiss()
//        videoItem?.let {
//            it.seekTime = binding.playerView.player?.currentPosition ?: seekTime
//            Log.e("time", "seekTime = ${it.seekTime}")
//            callback?.onDismiss(it)
//        }
    }
}
