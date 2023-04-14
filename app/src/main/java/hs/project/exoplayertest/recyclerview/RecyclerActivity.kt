package hs.project.exoplayertest.recyclerview

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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
            override fun onPlay(item: RecyclerItem) {
                play(item.path)
                Toast.makeText(this@RecyclerActivity, item.videoNo, Toast.LENGTH_SHORT).show()
            }
        })

        with(binding.rvList) {
            adapter = recycleAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setData() {
        for (i in 0..10) {
            recycleList.add(
                RecyclerItem(i, getString(R.string.media_url_mp4))
            )
        }
    }

    private fun play(url: String) {
        val dataSourceFactory = DefaultDataSourceFactory(this)
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(url)))
        /*player?.apply {
            setMediaSource(mediaSource)
            prepare()
            play()
        }*/
    }
}