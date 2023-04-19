package hs.project.exoplayertest.recyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import hs.project.exoplayertest.R
import hs.project.exoplayertest.databinding.ActivityRvMainBinding

class RvMainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityRvMainBinding.inflate(layoutInflater) }

    private lateinit var mainAdapter: RvMainAdapter

    private val test1 = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    private val test2 = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
    private val test3 = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"

    private val videos = arrayListOf<RvModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        videos.add(RvModel(test1, 0L, false))
        videos.add(RvModel(test2, 0L, false))
        videos.add(RvModel(test3, 0L, false))

        mainAdapter = RvMainAdapter(object : RvMainAdapter.Callback {
            override fun callback(selected: ArrayList<RvMainAdapter.ViewHolder>) {

            }
        })

        with(binding.rvList) {
            adapter = mainAdapter
            layoutManager = LinearLayoutManager(this@RvMainActivity)
        }

        mainAdapter.datas = videos
    }
}