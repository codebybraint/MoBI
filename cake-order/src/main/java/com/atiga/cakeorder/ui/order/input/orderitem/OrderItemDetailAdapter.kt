package com.atiga.cakeorder.ui.order.input.orderitem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.order.OrderAddItemDetail
import com.atiga.cakeorder.databinding.ItemOrderItemDetailBinding

class OrderItemDetailAdapter: RecyclerView.Adapter<OrderItemDetailAdapter.ViewHolder>() {
    var listOrderItemData = mutableListOf<OrderAddItemDetail>()

    fun setData(data: MutableList<OrderAddItemDetail>) {
        listOrderItemData = data
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemOrderItemDetailBinding.bind(itemView)
        fun bind(data: OrderAddItemDetail){
            with(binding){
                tvOrderitemQty.text = data.quantity.toString()
                tvOrderitemDesc.text = data.description

                data.decoration?.let { decor ->
                    tvOrderitemUcapan.text = decor.ucapan
                } ?: run {
                    tvOrderitemUcapan.visibility = View.GONE
                    tvTitleUcapan.visibility = View.GONE
                }

                data.images?.let {
                    if(it.isNotEmpty()){
                        tvImageCount.visibility = View.VISIBLE
                        tvImageCount.text = it.size.toString()
                    }
                } ?: kotlin.run {
                    tvImageCount.visibility = View.GONE
                }
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_order_item_detail, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listOrderItemData[position]
        holder.bind(data)
    }

    override fun getItemCount() = listOrderItemData.size
}