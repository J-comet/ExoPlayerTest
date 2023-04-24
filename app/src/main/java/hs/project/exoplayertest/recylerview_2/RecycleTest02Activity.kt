package hs.project.exoplayertest.recylerview_2

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import hs.project.exoplayertest.databinding.ActivityRecycleTest02Binding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecycleTest02Activity : AppCompatActivity() {

    private val binding by lazy { ActivityRecycleTest02Binding.inflate(layoutInflater) }

    private lateinit var test02Adapter : RecycleTest02Adapter

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

    private var previousPlayingId = 0

    private val videos = arrayListOf<TestVideo02>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initRecyclerView()
    }

    private fun showSystemUI() {
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, binding.root).show(WindowInsetsCompat.Type.systemBars())
    }

    private suspend fun updateVideoThumbnail(item: TestVideo02): Bitmap? {
        return withContext(Dispatchers.Main) {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(item.path)

            val bitmap = try {
                mediaMetadataRetriever.getFrameAtTime(item.seekTime * 1000L)
            } catch (e: Exception) {
                null
            }
            bitmap
        }
    }

//    private fun updateVideoThumbnail(item: TestVideo02): Bitmap? {
//        val mediaMetadataRetriever = MediaMetadataRetriever()
//        mediaMetadataRetriever.setDataSource(item.path)
//
//        val bitmap = try {
//            Log.e("bitmap", "VideoThumbnail / ${item.seekTime}")
//            mediaMetadataRetriever.getFrameAtTime(item.seekTime * 1000L)
//        } catch (e: Exception) {
//            Log.e("bitmap", "error bitmap = / ".plus(e.stackTraceToString()))
//            null
//        }
//        return bitmap
//    }

    private fun showFullScreenDialog(item: TestVideo02) {
        val screenDialog = FullScreenDialog().newInstance(item)
        screenDialog.setFullScreenCallback(object : FullScreenDialog.FullScreenCallback {
            override fun onDismiss(fullItem: TestVideo02) {
                showSystemUI()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

                run {
                    videos.forEachIndexed { index, testVideo02 ->
                        if (testVideo02.id == fullItem.id) {
                            videos[index] = fullItem.copy(seekTime = fullItem.seekTime)
                            return@run
                        }
                    }
                }

                test02Adapter.submitList(videos.toList())

            }
        })
        supportFragmentManager.beginTransaction().add(screenDialog, FullScreenDialog.TAG).commitAllowingStateLoss()
    }

    private fun initRecyclerView() {
        videos.add(TestVideo02(1, test1,false,0L, testImg1, null))
        videos.add(TestVideo02(2, test2,false,0L, testImg2, null))
        videos.add(TestVideo02(3, test3,false,0L, testImg3, null))
        videos.add(TestVideo02(4, test1,false,0L, testImg4, null))
        videos.add(TestVideo02(5, test2,false,0L, testImg5, null))
        videos.add(TestVideo02(6, test3,false,0L, testImg6, null))
        videos.add(TestVideo02(7, test1,false,0L, testImg1, null))
        videos.add(TestVideo02(8, test2,false,0L, testImg2, null))
        videos.add(TestVideo02(9, test3,false,0L, testImg3, null))

        test02Adapter = RecycleTest02Adapter(
            fullScreen = { item ->
                showFullScreenDialog(item)
            },

            playChange = { isPlay, selectItem ->

                if (previousPlayingId == 0) {

                } else {
                    // 썸네일 따기

                }

                lifecycleScope.launch {
                    videos.forEachIndexed { index, testVideo02 ->
                        if (testVideo02.id == selectItem.id) {
//                            val time = measureTimeMillis {
//                                updateVideoThumbnail(selectItem)
//                            }
//                            Log.e("ㅈㅂㄷ", "걸린시간 측정 ".plus(time))
                            videos[index] = selectItem.copy(playWhenReady = isPlay, videoThumbnail = updateVideoThumbnail(selectItem))
                        } else {
                            if (previousPlayingId == testVideo02.id) {
                                Log.e("previousPlayingId", "previousPlayingId ".plus(previousPlayingId))
                                videos[index] = testVideo02.copy(playWhenReady = false, videoThumbnail = updateVideoThumbnail(testVideo02))
                            } else {
                                videos[index] = testVideo02.copy(playWhenReady = false)
                            }
                        }
                    }

                    test02Adapter.submitList(videos.toList())
                }


                if (isPlay) {
                    Toast.makeText(this, selectItem.id.toString().plus("/ 플레이 ".plus(previousPlayingId)), Toast.LENGTH_SHORT).show()
                    previousPlayingId = selectItem.id
                } else {
                    Toast.makeText(this, selectItem.id.toString().plus("/ 일시정지 ".plus(previousPlayingId)), Toast.LENGTH_SHORT).show()
                    previousPlayingId = 0
                }
            }
        )

        binding.rvList.apply {
            adapter = test02Adapter
            layoutManager = LinearLayoutManager(context)
//            addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                    super.onScrollStateChanged(recyclerView, newState)
//
//                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                    when (newState) {
//                        RecyclerView.SCROLL_STATE_IDLE -> {
//                            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
//                            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
//                            for (i in firstVisibleItemPosition..lastVisibleItemPosition) {
//                                val holder = recyclerView.findViewHolderForAdapterPosition(i) as? RecycleTest02Adapter.ViewHolder
//                                holder?.exoPlayer?.playWhenReady = true
//                            }
//                        }
//                        else -> {
//                            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
//                            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
//                            for (i in firstVisibleItemPosition..lastVisibleItemPosition) {
//                                val holder = recyclerView.findViewHolderForAdapterPosition(i) as? RecycleTest02Adapter.ViewHolder
//                                holder?.exoPlayer?.playWhenReady = false
//                            }
//                        }
//                    }
//                }
//            })

            /**
             * 스크롤 Idle 상태일 때 recyclerview seekTime 업데이트 하는 코드
             */
//            addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                    super.onScrollStateChanged(recyclerView, newState)
//                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
//                        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
//                        for (i in firstVisiblePosition..lastVisiblePosition) {
//
//                            val holder = recyclerView.findViewHolderForAdapterPosition(i) as? RecycleTest02Adapter.ViewHolder
//                            holder?.let {
//                                val item = videos[i]
//                                val currentPosition = it.exoPlayer?.currentPosition ?: 0L
//                                item.seekTime = currentPosition
//                                Log.e("11", "item.seekTime / ${item.seekTime}")
//                            }
//                        }
//
//                    } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
//
//                    }
//                }
//            })
        }

        test02Adapter.submitList(videos.toList())
    }
}