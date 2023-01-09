package com.atiga.cakeorder.ui.report.track

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.order.ItemOrder
import com.atiga.cakeorder.databinding.ItemDetailOrderBinding
import java.text.SimpleDateFormat

class TrackOrderAdapter: RecyclerView.Adapter<TrackOrderAdapter.ViewHolder>() {
    var onItemClick: ((String, Int, String, Boolean) -> Unit)? = null
    var listItemData = listOf<ItemOrder>()

    fun setData(data: List<ItemOrder>) {
        listItemData = data
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemDetailOrderBinding.bind(itemView)
        private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        private val outputDateFormat = SimpleDateFormat("d MMM yyyy")

        fun bind(data: ItemOrder) {
            with(binding) {
                tvDetailitemName.text = data.namaBarang
                tvDetailitemQty.text = data.jumlah.toString()
                tvDetailitemKeterangan.text = data.keterangan
                data.dekorasi?.let {
                    tvDetailitemUcapan.text = it.ucapan
                    tvDetailitemUcapan.visibility = View.VISIBLE
                    tvDetailitemUcapanTitle.visibility = View.VISIBLE
                } ?: run {
                    tvDetailitemUcapan.visibility = View.GONE
                    tvDetailitemUcapanTitle.visibility = View.GONE
                }
                data.jobStarted?.let {
                    if(it){
                        viewOrderColorStatus.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.orderitem_start_dekor))
                    }
                }
                data.finishDate?.let {
                    tvDetailitemTanggalSelesai.text = outputDateFormat.format(inputDateFormat.parse(it))
                    viewOrderColorStatus.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.orderitem_finish_dekor))
                }
                data.hasBeenCanceled?.let {
                    if(it){
                        viewOrderColorStatus.setBackgroundColor(ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark))
                    }
                }

                data.pickupDate?.let {
                    btnPickItem.visibility = View.GONE
                    viewOrderColorStatus.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.orderitem_finish_order))
                }
            }
        }

        init {
            binding.cardOrderitemDetail.setOnClickListener {
                listItemData[adapterPosition].noPesanan?.let { orderNumber ->
                    onItemClick?.invoke(
                        orderNumber,
                        listItemData[adapterPosition].itemKey!!,
                        listItemData[adapterPosition].namaBarang!!,
                        listItemData[adapterPosition].hasBeenCanceled!!
                    )
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_detail_order, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listItemData[position]
        holder.bind(data)
    }

    override fun getItemCount() = listItemData.size
}