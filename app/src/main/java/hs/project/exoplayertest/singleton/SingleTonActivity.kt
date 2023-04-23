package hs.project.exoplayertest.singleton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hs.project.exoplayertest.R
import hs.project.exoplayertest.databinding.ActivitySingleTonBinding
import hs.project.exoplayertest.recylerview_2.RecycleTest02Adapter
import hs.project.exoplayertest.recylerview_2.TestVideo02

class SingleTonActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySingleTonBinding.inflate(layoutInflater) }

    private val test1 =
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    private val test2 =
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
    private val test3 =
        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"

    private val testImg1 =
        "https://upload.wikimedia.org/wikipedia/commons/thumb/7/76/Antarctica_2013_Journey_to_the_Crystal_Desert_%288370623298%29.jpg/640px-Antarctica_2013_Journey_to_the_Crystal_Desert_%288370623298%29.jpg"
    private val testImg2 =
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTEA8UUaN6iMOmJZ4AuemmJC-6l5KkbMF6hRQTjYdAEVA&s"
    private val testImg3 =
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQKtT-E4H3IFqI0BOKpKkx5tol6ZsbI-yayem0ElObL8Q&s"
    private val testImg4 =
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSmjQ7zzv-tO12sXMc_2a9S9mdTKWdM3WRg9ga3VpytHQ&s"
    private val testImg5 =
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRI4r7SvF2cbj7GoGBCiz_sO6RKmlq0pSDp9mtXcn98cQ&s"
    private val testImg6 =
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTT1tVXGbSUlPA_7fukWf-9yS8evxU07RctMZ7nibUwCw&s"

    private val videos = arrayListOf<SingleTonVideo>()
    private lateinit var singleTonAdapter: SingleTonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initRecyclerView()
    }

    private fun initRecyclerView() {
        videos.add(SingleTonVideo(1, test1,false,0L, testImg1, null))
        videos.add(SingleTonVideo(2, test2,false,0L, testImg2, null))
        videos.add(SingleTonVideo(3, test3,false,0L, testImg3, null))
        videos.add(SingleTonVideo(4, test1,false,0L, testImg4, null))
        videos.add(SingleTonVideo(5, test2,false,0L, testImg5, null))
        videos.add(SingleTonVideo(6, test3,false,0L, testImg6, null))
        videos.add(SingleTonVideo(7, test1,false,0L, testImg1, null))
        videos.add(SingleTonVideo(8, test2,false,0L, testImg2, null))
        videos.add(SingleTonVideo(9, test3,false,0L, testImg3, null))


        singleTonAdapter = SingleTonAdapter(
            fullScreen = { item ->
                Toast.makeText(this, item.id.toString().plus(" ").plus(item.seekTime), Toast.LENGTH_SHORT).show()
            },

            playChange = { isPlay, selectItem ->

                videos.forEachIndexed { index, testVideo02 ->

                    if (testVideo02.id == selectItem.id) {
                        videos[index] = selectItem.copy(playWhenReady = isPlay)
//                        videos[index] = selectItem.copy(playWhenReady = isPlay, videoThumbnail = updateVideoThumbnail(selectItem))
                    } else {
                        videos[index] = testVideo02.copy(playWhenReady = false)
//                        videos[index] = testVideo02.copy(playWhenReady = false, videoThumbnail = updateVideoThumbnail(testVideo02))
                    }
                }

                if (isPlay) {
                    Toast.makeText(this, selectItem.id.toString().plus("/ 플레이"), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, selectItem.id.toString().plus("/ 일시정지"), Toast.LENGTH_SHORT).show()
                }

                singleTonAdapter.submitList(videos.toList())
            }
        )

        binding.rvList.apply {
            adapter = singleTonAdapter
            layoutManager = LinearLayoutManager(context)
        }

        singleTonAdapter.submitList(videos.toList())
    }
}