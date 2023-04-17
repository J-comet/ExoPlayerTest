package hs.project.exoplayertest.custom_recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.exoplayer.model.MediaObject
import hs.project.exoplayertest.R

class MediaRecyclerAdapter(
    private val mediaObjects: ArrayList<MediaObject>
) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        return PlayerViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.layout_media_list_item, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        (viewHolder as PlayerViewHolder).onBind(mediaObjects[i])
    }

    override fun getItemCount(): Int {
        return mediaObjects.size
    }
}