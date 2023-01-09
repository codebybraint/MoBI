package com.atiga.cakeorder.ui.order.edit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.order.ItemOrder
import com.atiga.cakeorder.databinding.ItemDetailOrderBinding
import java.text.SimpleDateFormat

class EditOrderAdapter: RecyclerView.Adapter<EditOrderAdapter.ViewHolder>() {
    var listItemData = listOf<ItemOrder>()
    var onItemClick: ((ItemOrder, Int, Boolean) -> Unit)? = null

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
                data.images?.let {
                    if(it.isNotEmpty()){
                        tvImageCount.visibility = View.VISIBLE
                        tvImageCount.text = it.size.toString()
                    }
                } ?: kotlin.run {
                    tvImageCount.visibility = View.GONE
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
                data.pickupDate?.let {
                    btnPickItem.visibility = View.GONE
                    viewOrderColorStatus.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.orderitem_finish_order))
                }

            }
        }

        init {
            binding.cardOrderitemDetail.setOnClickListener {
                onItemClick?.invoke(
                    listItemData[adapterPosition],
                    adapterPosition,
                    listItemData[adapterPosition].flag!!
                            || !listItemData[adapterPosition].finishDate.isNullOrBlank()
                            || listItemData[adapterPosition].jobStarted!!
                    )
            }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_detail_order, parent, false)
        )

    override fun onBindViewHolder(holder: EditOrderAdapter.ViewHolder, position: Int) {
        val data = listItemData[position]
        holder.bind(data)
    }

    override fun getItemCount() = listItemData.size
}