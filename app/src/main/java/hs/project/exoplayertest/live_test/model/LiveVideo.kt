package hs.project.exoplayertest.live_test.model

data class LiveVideo(
    var videoNo: Int,
    var title: String,
    var explain: String,
    var courseType: Int,
    var viewpagerList : ArrayList<LiveVideoViewPager>,
    var playNo: Int
)

data class LiveVideoViewPager(
    var videoDetailNo: Int,
    var imagePath: String,  // 영상 썸네일
    var videoPath: String,  // 영상
    var currentWindow: Int,
    var playbackPosition: Long
//    var status: String  // 재생, 일시정지, 정지
)
