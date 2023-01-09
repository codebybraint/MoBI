package com.atiga.cakeorder.ui.report.track

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.order.Order
import com.atiga.cakeorder.databinding.ItemOrderBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime

class TrackResultAdapter: RecyclerView.Adapter<TrackResultAdapter.ViewHolder>() {
    var onItemClick: ((Order) -> Unit)? = null
    var listItemData = listOf<Order>()


    fun setData(data: List<Order>) {
        listItemData = data
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemOrderBinding.bind(itemView)
        private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        private val outputDateFormat = SimpleDateFormat("d MMM yyyy")
        fun bind(data: Order) {
            with(binding) {
                    tvOrderNumber.text = data.orderNumber
                    tvCustomer.text = data.customerName
                    data.orderDate?.let { tvOrderDate.text = outputDateFormat.format(inputDateFormat.parse(it))}
                    tvPhoneNumber.text = data.phoneNumber
                    if(data.isPaid!!){
                        tvLunas.text = "Lunas"
                        tvLunas.setTextColor(itemView.resources.getColor(R.color.lunas))
                        tvLunas.setBackgroundResource(R.drawable.bg_lunas)
                    } else {
                        tvLunas.text = "DP"
                        tvLunas.setTextColor(itemView.resources.getColor(R.color.downpayment))
                        tvLunas.setBackgroundResource(R.drawable.bg_downpayment)
                    }
                }
            }
        init {
            binding.cardOrder.setOnClickListener {
                listItemData[adapterPosition].let { order -> onItemClick?.invoke(order) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listItemData[position]
        holder.bind(data)
    }

    override fun getItemCount() = listItemData.size
}