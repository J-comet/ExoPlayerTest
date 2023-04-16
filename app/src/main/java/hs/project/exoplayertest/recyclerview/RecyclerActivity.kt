package hs.project.exoplayertest.recyclerview

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import hs.project.exoplayertest.R
import hs.project.exoplayertest.databinding.ActivityRecyclerBinding

class RecyclerActivity : AppCompatActivity() {

    private val binding by lazy { ActivityRecyclerBinding.inflate(layoutInflater) }

    private val recycleAdapter by lazy { RecyclerAdapter() }

    private val recycleList = arrayListOf<RecyclerItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        setData()
        recycleAdapter.submitList(recycleList.toList())
    }

    private fun init() {
        recycleAdapter.setOnListener(object : RecyclerAdapter.OnRecyclerListener {
            override fun onPlay(item: RecyclerItem, exoPlayer: ExoPlayer?) {
                exoPlayer?.let {
                    it.playWhenReady = true
                    it.seekTo(item.currentWindow, item.playbackPosition)
                    it.prepare()
                }

                Toast.makeText(this@RecyclerActivity, item.videoNo, Toast.LENGTH_SHORT).show()
            }
        })

        with(binding.rvList) {
            adapter = recycleAdapter
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
        }
    }

    private fun setData() {
        for (i in 0..10) {
            recycleList.add(
                RecyclerItem(
                    i,
                    getString(R.string.media_url_mp4),
                    false,
                    0,
                    0L
                )
            )
        }
    }
}