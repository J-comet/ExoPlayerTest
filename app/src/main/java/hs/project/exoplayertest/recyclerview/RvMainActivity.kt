package hs.project.exoplayertest.recyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import hs.project.exoplayertest.R
import hs.project.exoplayertest.databinding.ActivityRvMainBinding

class RvMainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityRvMainBinding.inflate(layoutInflater) }

    private lateinit var mainAdapter: RvMainAdapter

    private val test1 =
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    private val test2 =
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
    private val test3 =
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"

    private val videos = arrayListOf<RvModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        videos.add(RvModel(1, test1, 0L, false))
        videos.add(RvModel(2, test2, 0L, false))
        videos.add(RvModel(3, test3, 0L, false))

        mainAdapter = RvMainAdapter(object : RvMainAdapter.Callback {
            override fun callback(selectedItem: RvModel) {

                Log.e("3", "33 / ${mainAdapter.viewHolders.size}")
                Log.e("3", "34 / ${videos.size}")

                videos.forEachIndexed { index, item ->
                    if (selectedItem.id == item.id) {
                        videos[index] = videos[index].copy(playWhenReady = !item.playWhenReady)
                    } else {
                        videos[index] = videos[index].copy(playWhenReady = false)
                    }
                }

                Log.e("3", "35 / ${videos}")

                mainAdapter.datas = videos
                mainAdapter.notifyDataSetChanged()

                Toast.makeText(this@RvMainActivity, selectedItem.id.toString(), Toast.LENGTH_SHORT).show()
            }
        })

        with(binding.rvList) {
            adapter = mainAdapter
            layoutManager = LinearLayoutManager(this@RvMainActivity)
        }

        mainAdapter.datas = videos
    }
}