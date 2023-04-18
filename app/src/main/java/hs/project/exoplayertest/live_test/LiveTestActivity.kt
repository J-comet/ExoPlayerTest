package hs.project.exoplayertest.live_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import hs.project.exoplayertest.R
import hs.project.exoplayertest.databinding.ActivityLiveTestBinding
import hs.project.exoplayertest.live_test.model.LiveVideo
import hs.project.exoplayertest.live_test.model.LiveVideoViewPager

class LiveTestActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLiveTestBinding.inflate(layoutInflater) }

    private val liveTestAdapter by lazy { LiveTestAdapter() }

    private val liveTestList = arrayListOf<LiveVideo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setData()

        with(binding.rvList) {
            adapter = liveTestAdapter
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
        }

        liveTestAdapter.submitList(liveTestList.toList())
    }

    private fun setData() {
        val image = ArrayList<String>()
        for (i in 0 until 5) {
            if (i % 2 == 0) {
                image.add("https://thumb2.gettyimageskorea.com/image_preview/700/201807/EYM/1004485380.jpg")
            } else {
                image.add("https://thumb2.gettyimageskorea.com/image_preview/700/202302/FPX/1460011271.jpg")
            }
        }

        val videoList = ArrayList<String>()
        for (i in 0 until 5) {
            val url = when(i % 3) {
                0 -> "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
                1 -> "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
                else -> "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
            }
            videoList.add(url)
        }

        val viewpagerList = arrayListOf<LiveVideoViewPager>()
        image.forEachIndexed { index, image ->
            viewpagerList.add(
                LiveVideoViewPager(
                    index,
                    image,
                    videoList[index],
                    0,
                    0L
                )
            )
        }

        for (i in 0 until 20) {
            val video = LiveVideo(
                videoNo = i,
                title = "title[$i]",
                explain = "[$i]",
                courseType = i + 1,
                viewpagerList = viewpagerList,
                playNo = -1
            )
            liveTestList.add(video)
        }
    }
}