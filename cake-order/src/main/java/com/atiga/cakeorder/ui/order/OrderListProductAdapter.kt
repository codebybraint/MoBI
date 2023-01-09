package com.atiga.cakeorder.ui.order

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.order.ItemOrder
import com.atiga.cakeorder.core.domain.model.order.OrderAddItemDetail
import com.atiga.cakeorder.databinding.ItemOrderImagesBinding
import com.atiga.cakeorder.databinding.ItemOrderItemDetailCardBinding
import com.atiga.cakeorder.databinding.ItemOrderProductBinding
import com.atiga.cakeorder.ui.order.input.orderitem.ListOrderItemAdapter

class OrderListProductAdapter:  RecyclerView.Adapter<OrderListProductAdapter.ViewHolder>(){
    var listOrderProduct = mutableListOf<ItemOrder>()

    fun setData(data: MutableList<ItemOrder>) {
        listOrderProduct = data
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemOrderProductBinding.bind(itemView)
        fun bind(data: ItemOrder){
            with(binding){
                tvProductName.text = data.namaBarang
                tvProductQty.text = "${data.jumlah} Pcs"
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_order_product, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listOrderProduct[position]
        holder.bind(data)
    }

    override fun getItemCount() = listOrderProduct.size
}