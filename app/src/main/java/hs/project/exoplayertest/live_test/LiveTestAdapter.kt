package hs.project.exoplayertest.live_test

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.exoplayer2.ExoPlayer
import hs.project.exoplayertest.databinding.ItemLiveVideoBinding
import hs.project.exoplayertest.live_test.model.LiveVideo

class LiveTestAdapter :
    ListAdapter<LiveVideo, LiveTestAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<LiveVideo?>() {
            override fun areItemsTheSame(
                oldItem: LiveVideo,
                newItem: LiveVideo,
            ): Boolean {
                return oldItem.videoNo == newItem.videoNo
            }

            override fun areContentsTheSame(
                oldItem: LiveVideo,
                newItem: LiveVideo,
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLiveVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
//        holder.itemBinding.playerView.player?.release()
    }

    inner class ViewHolder(
        val itemBinding: ItemLiveVideoBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private var player: ExoPlayer? = null

        fun bind(item: LiveVideo) {
            player = ExoPlayer.Builder(itemBinding.root.context).build()
            itemBinding.tvPage.text =
                "1/${item.viewpagerList.size}"

            itemBinding.vpVideo.id = bindingAdapterPosition + 1
            val viewPagerAdapter = LiveTestViewpagerAdapter(player)

            with(itemBinding.vpVideo) {
                adapter = viewPagerAdapter
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
                currentItem = 0
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        itemBinding.tvPage.text =
                            "${position + 1}/${item.viewpagerList.size}"
                    }
                })
            }
            viewPagerAdapter.submitList(item.viewpagerList.toList())
        }
    }
}