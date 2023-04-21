package hs.project.exoplayertest.recylerview_2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import hs.project.exoplayertest.R
import hs.project.exoplayertest.databinding.ActivityRecycleTest02Binding
import hs.project.exoplayertest.recyclerview.RvModel

class RecycleTest02Activity : AppCompatActivity() {

    private val binding by lazy { ActivityRecycleTest02Binding.inflate(layoutInflater) }

    private lateinit var test02Adapter : RecycleTest02Adapter

    private val test1 =
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    private val test2 =
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
    private val test3 =
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"

    private val videos = arrayListOf<TestVideo02>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        videos.add(TestVideo02(1, test1,false,0L))
        videos.add(TestVideo02(2, test2,false,0L))
        videos.add(TestVideo02(3, test3,false,0L))
        videos.add(TestVideo02(4, test1,false,0L))
        videos.add(TestVideo02(5, test2,false,0L))
        videos.add(TestVideo02(6, test3,false,0L))

        /**
         *  TODO 선택되지 않은 item 값들의 seekTime 을 어떻게 가져올 것인가
         */
        test02Adapter = RecycleTest02Adapter(
            playChange = { isPlay, selectItem ->

                videos.forEachIndexed { index, testVideo02 ->
                    if (testVideo02.id == selectItem.id) {
                        videos[index] = selectItem.copy(playWhenReady = isPlay)
                    } else {
                        videos[index] = testVideo02.copy(playWhenReady = false)
                    }
                }

                if (isPlay) {
                    Toast.makeText(this, selectItem.id.toString().plus("/ 플레이"), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, selectItem.id.toString().plus("/ 일시정지"), Toast.LENGTH_SHORT).show()
                }

                test02Adapter.submitList(videos.toList())
            }
        )

        binding.rvList.apply {
            adapter = test02Adapter
            layoutManager = LinearLayoutManager(context)
        }

        test02Adapter.submitList(videos.toList())
    }
}