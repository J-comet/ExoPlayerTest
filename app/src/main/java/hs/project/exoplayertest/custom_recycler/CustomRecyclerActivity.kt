package hs.project.exoplayertest.custom_recycler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exoplayer.model.MediaObject
import hs.project.exoplayertest.R
import hs.project.exoplayertest.databinding.ActivityCustomRecyclerBinding
import java.util.ArrayList

class CustomRecyclerActivity : AppCompatActivity() {

    private val binding by lazy { ActivityCustomRecyclerBinding.inflate(layoutInflater) }

    private val mediaObjectList = ArrayList<MediaObject>()
    private var mAdapter: MediaRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        prepareVideoList()
        mAdapter = MediaRecyclerAdapter(mediaObjectList)


    }

    override fun onStart() {
        super.onStart()
        with(binding.exoPlayerRecyclerView) {
            adapter = mAdapter
            smoothScrollToPosition(1)
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroy() {
        binding.exoPlayerRecyclerView.releasePlayer()
        super.onDestroy()
    }

    private fun prepareVideoList() {
        val mediaObject = MediaObject()
        mediaObject.id = 1
        mediaObject.userHandle = "User 1"
        mediaObject.title = "Item 1"
        mediaObject.coverUrl =
            "https://www.muscleandfitness.com/wp-content/uploads/2019/04/7-Demonized-BodyBuilding-Food-Gallery.jpg?w=940&h=529&crop=1"
        mediaObject.url =
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
        val mediaObject2 = MediaObject()
        mediaObject2.id = 2
        mediaObject2.userHandle = "user 2"
        mediaObject2.title = "Item 2"
        mediaObject2.coverUrl =
            "https://www.muscleandfitness.com/wp-content/uploads/2019/04/7-Demonized-BodyBuilding-Food-Gallery.jpg?w=940&h=529&crop=1"
        mediaObject2.url =
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
        val mediaObject3 = MediaObject()
        mediaObject3.id = 3
        mediaObject3.userHandle = "User 3"
        mediaObject3.title = "Item 3"
        mediaObject3.coverUrl =
            "https://www.muscleandfitness.com/wp-content/uploads/2019/04/7-Demonized-BodyBuilding-Food-Gallery.jpg?w=940&h=529&crop=1"
        mediaObject3.url =
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
        val mediaObject4 = MediaObject()
        mediaObject4.id = 4
        mediaObject4.userHandle = "User 4"
        mediaObject4.title = "Item 4"
        mediaObject4.coverUrl =
            "https://www.muscleandfitness.com/wp-content/uploads/2019/04/7-Demonized-BodyBuilding-Food-Gallery.jpg?w=940&h=529&crop=1"
        mediaObject4.url =
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
        val mediaObject5 = MediaObject()
        mediaObject5.id = 5
        mediaObject5.userHandle = "User 5"
        mediaObject5.title = "Item 5"
        mediaObject5.coverUrl =
            "https://www.muscleandfitness.com/wp-content/uploads/2019/04/7-Demonized-BodyBuilding-Food-Gallery.jpg?w=940&h=529&crop=1"
        mediaObject5.url =
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"
        mediaObjectList.add(mediaObject)
        mediaObjectList.add(mediaObject2)
        mediaObjectList.add(mediaObject3)
        mediaObjectList.add(mediaObject4)
        mediaObjectList.add(mediaObject5)
    }
}