package hs.project.exoplayertest.lifecycle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import hs.project.exoplayertest.databinding.ActivityRecyclerBinding

class LifeCycleActivity : AppCompatActivity() {

    private val binding by lazy { ActivityRecyclerBinding.inflate(layoutInflater) }

    private lateinit var lifecycleAdapter: LifecycleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val videos = listOf(
            "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_10mb.mp4",
            "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_20mb.mp4",
            "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_50mb.mp4"
        )

        lifecycleAdapter = LifecycleAdapter(videos, this)

        with(binding.rvList) {
            adapter = lifecycleAdapter
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
        }
    }
}