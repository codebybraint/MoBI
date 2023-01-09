package com.atiga.cakeorder.ui.order.input.orderitem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.order.OrderAddItem
import com.atiga.cakeorder.core.domain.model.order.OrderAddItemDetail
import com.atiga.cakeorder.databinding.ItemAddedOrderProductBinding

class AddedItemAdapter: RecyclerView.Adapter<AddedItemAdapter.ViewHolder>() {
    var onItemClick: ((List<OrderAddItemDetail>?, String, Int, Int) -> Unit)? = null
    var onCopyItemClick: ((String) -> Unit)? = null
    var listItemData = mutableListOf<OrderAddItem>()

    fun setData(data: MutableList<OrderAddItem>) {
        listItemData = data
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemAddedOrderProductBinding.bind(itemView)
        val orderItemDetailAdapter = OrderItemDetailAdapter()

        fun bind(data: OrderAddItem) {
            with(binding) {
                tvName.text = data.productName
                tvQuantity.text = data.totalQuantity.toString()
            }
        }

        init {
//            binding.btnCopyDesc.setOnClickListener {
//                onCopyItemClick?.invoke(binding.tvDesc.text.toString())
//            }

            with(binding.rvDecoration) {
                layoutManager = LinearLayoutManager(
                    itemView.context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                setHasFixedSize(true)
                adapter = orderItemDetailAdapter
            }

            binding.btnEdit.setOnClickListener {
                onItemClick?.invoke(listItemData[adapterPosition].listOrderItem, listItemData[adapterPosition].productName, listItemData[adapterPosition].totalQuantity, adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_added_order_product, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listItemData[position]
        holder.bind(data)
        val itemHolder = holder as ViewHolder
        listItemData[position].listOrderItem?.let { itemHolder.orderItemDetailAdapter.setData(it) }
    }

    override fun getItemCount() = listItemData.size
}