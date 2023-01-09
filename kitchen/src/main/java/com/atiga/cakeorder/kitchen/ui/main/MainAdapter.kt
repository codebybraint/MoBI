package com.atiga.cakeorder.kitchen.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atiga.cakeorder.core.network.response.KitchenOrderResponseItem
import com.atiga.cakeorder.kitchen.R
import com.atiga.cakeorder.kitchen.databinding.ItemOrderitemBinding
import java.text.SimpleDateFormat

class MainAdapter : RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    var onItemClick: ((KitchenOrderResponseItem, Boolean) -> Unit)? = null
    var orderData = mutableListOf<KitchenOrderResponseItem>()
    var sortedOrder = mutableListOf<KitchenOrderResponseItem>()

    fun setData(data: MutableList<KitchenOrderResponseItem>) {
        orderData = data
        orderData.sortBy { tglAmbil -> tglAmbil.tglAmbil }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemOrderitemBinding.bind(itemView)
        private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        private val outputDateFormat = SimpleDateFormat("d MMM yyyy, HH:mm")

        fun bind(data: KitchenOrderResponseItem) {
            with(binding) {
                tvOrderitemName.text = data.namaBarang
                tvNoOrder.text = data.noPesanan
                tvKeterangan.text = data.keterangan
                data.dekorasi?.let { tvUcapan.text = it.ucapan } ?: run {
                    tvUcapan.visibility = View.GONE
                    tvUcapanTitle.visibility = View.GONE
                }

                data.tglAmbil?.let { tvOrderTglambil.text = outputDateFormat.format(inputDateFormat.parse(it)) }

                data.jobStarted?.let { stat ->
                    if (stat) {
                        viewStat.setBackgroundResource(R.color.orderitem_start_dekor)
                    } else {
                        viewStat.setBackgroundResource(R.color.orderitem_new)
                    }
                }

//                if(!data.tanggalSelesai.isNullOrBlank()){
//                    viewStat.setBackgroundResource(R.color.orderitem_finish_dekor)
//                }

                data.detail.tglSelesai?.let {
                    viewStat.setBackgroundResource(R.color.orderitem_finish_dekor)
                }

                data.images?.let {
                    if(it.isNotEmpty()) {
                        tvImageCount.text = it.size.toString()
                        tvImageCount.visibility = View.VISIBLE
                    }
                } ?: kotlin.run {
                    tvImageCount.visibility = View.GONE
                }
            }
        }

        init {
            binding.cardOrderitemDetail.setOnClickListener {
                onItemClick?.invoke(
                    orderData[adapterPosition],
                    orderData[adapterPosition].flag!!
                            || !orderData[adapterPosition].detail.tglSelesai.isNullOrBlank()
                    )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_orderitem, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = orderData[position]
        holder.bind(data)
    }

    override fun getItemCount() = orderData.size
}