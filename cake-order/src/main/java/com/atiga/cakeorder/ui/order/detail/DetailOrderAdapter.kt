package com.atiga.cakeorder.ui.order.detail

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.order.ItemOrder
import com.atiga.cakeorder.databinding.ItemDetailOrderBinding
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class DetailOrderAdapter: RecyclerView.Adapter<DetailOrderAdapter.ViewHolder>() {
    var onItemClick: ((Boolean, Boolean, Boolean) -> Unit)? = null
    var onButtonPickItem: ((String, Int, String) -> Unit)? = null
    var listItemData = listOf<ItemOrder>()
    var pickupDate: String = ""

    fun setData(data: List<ItemOrder>) {
        listItemData = data
    }

    fun setPickupDateNonDekor(date: String?){
        pickupDate = if(date.isNullOrBlank()) pickupDate else date
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemDetailOrderBinding.bind(itemView)
        private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        private val outputDateFormat = SimpleDateFormat("d MMM yyyy")

        fun bind(data: ItemOrder) {
            with(binding) {
                tvDetailitemName.text = data.namaBarang
                tvDetailitemQty.text = data.jumlah.toString()
//                data.tanggalAmbil?.let { tvDetailitemTanggalAmbil.text = outputDateFormat.format(inputDateFormat.parse(it)) }
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
               // tgl finish kosong
                if(!listItemData[adapterPosition].finishDate.isNullOrBlank()){
                    listItemData[adapterPosition].finishDate?.let {
//                        val hariAmbil = outputDateFormat.format(inputDateFormat.parse(dat))
//                        val hariIni = outputDateFormat.format(inputDateFormat.parse(LocalDateTime.now().toString()))
//                        if(hariAmbil <= hariIni){
                            viewOrderColorStatus.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.orderitem_finish_dekor))
                            btnPickItem.visibility = View.VISIBLE
//                        }
                    }
                }
                data.pickupDate?.let {
                    btnPickItem.visibility = View.GONE
                    viewOrderColorStatus.setBackgroundColor(ContextCompat.getColor(itemView.context,
                        R.color.orderitem_finish_order))
                }

            }
        }

        init {
            binding.cardOrderitemDetail.setOnClickListener {
                listItemData[adapterPosition].noPesanan?.let { orderNumber ->
                    onItemClick?.invoke(
                        !listItemData[adapterPosition].finishDate.isNullOrBlank(),
                        listItemData[adapterPosition].jobStarted!!,
                        !listItemData[adapterPosition].pickupDate.isNullOrBlank()
                    )
                }
            }
            binding.btnPickItem.setOnClickListener {
                listItemData[adapterPosition].noPesanan?.let { orderNumber ->
                    onButtonPickItem?.invoke(
                        orderNumber,
                        listItemData[adapterPosition].itemKey!!,
                        listItemData[adapterPosition].keterangan!!
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