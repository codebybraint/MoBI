package com.atiga.cakeorder.ui.report.track.detail

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.enum.OrderStatus
import com.atiga.cakeorder.core.domain.model.track.Tracking
import com.atiga.cakeorder.core.utils.VectorDrawableUtil
import com.atiga.cakeorder.databinding.ItemTimelineTrackBinding
import java.text.SimpleDateFormat

class DetailTrackOrderAdapter(): RecyclerView.Adapter<DetailTrackOrderAdapter.ViewHolder>() {
    var listTracking = listOf<Tracking>()

    fun setData(data: List<Tracking>) {
        listTracking = data
    }

    inner class ViewHolder(itemView: View, viewType: Int): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemTimelineTrackBinding.bind(itemView)
        private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        private val outputDateFormat = SimpleDateFormat("d MMM yyyy")

        fun bind(data: Tracking){
            with(binding){
                data.trackingDate?.let {
                    tvTimelineDate.text = outputDateFormat.format(inputDateFormat.parse(it))
                } ?: kotlin.run {
                    tvTimelineDate.visibility = View.GONE
                }
                tvTimelineTitle.text = data.trackingStatus

                when{
                    data.trackingActiveStatus == OrderStatus.INACTIVE.id -> {
                        binding.timeline.marker = VectorDrawableUtil.getDrawable(itemView.context, R.drawable.marker, ContextCompat.getColor(itemView.context, R.color.material_grey_500))
                    }
                    data.trackingActiveStatus == OrderStatus.ACTIVE.id -> {
                        binding.timeline.marker = VectorDrawableUtil.getDrawable(itemView.context, R.drawable.ic_marker_active, ContextCompat.getColor(itemView.context, R.color.blue_dark))
                        binding.tvTimelineTitle.setTextColor(androidx.core.content.ContextCompat.getColor(itemView.context, android.R.color.black))
                        binding.tvTimelineTitle.setTypeface(null, Typeface.BOLD)
                    }
                    else -> {
                        binding.timeline.marker = VectorDrawableUtil.getDrawable(itemView.context, R.drawable.ic_marker_inactive, ContextCompat.getColor(itemView.context, R.color.material_grey_500))
                    }
                }
            }
        }
        init {
            binding.timeline.initLine(viewType)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_timeline_track, parent, false), viewType
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listTracking[position]
        holder.bind(data)
    }

    override fun getItemCount() = listTracking.size
}