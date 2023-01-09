package com.atiga.cakeorder.ui.report.detail

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.order.ItemOrder
import com.atiga.cakeorder.core.domain.model.order.Order
import com.atiga.cakeorder.databinding.ItemOrderBinding
import com.atiga.cakeorder.ui.order.OrderListProductAdapter
import java.text.SimpleDateFormat

class DetailReportAdapter: RecyclerView.Adapter<DetailReportAdapter.ViewHolder>() {
    var onItemClick: ((Order) -> Unit)? = null
    var orderData = mutableListOf<Order>()
    var orderListProduct = mutableListOf<ItemOrder>()
    var orderListProductAdapter = OrderListProductAdapter()

    fun setData(data: MutableList<Order>) {
        orderData = data
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemOrderBinding.bind(itemView)
        private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        private val outputDateFormat = SimpleDateFormat("d MMM yyyy")
        fun bind(data: Order) {
            with(binding) {
                tvOrderNumber.text = data.orderNumber
                tvCustomer.text = data.customerName
                orderListProduct = data.items.toMutableList()
                try{
                    orderListProductAdapter.setData(orderListProduct)
                    with(binding.rvOrderitemProduct){
                        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        setHasFixedSize(true)
                        adapter = orderListProductAdapter
                    }
                }catch (e: Exception){
                    Log.e("ERROR_CODE", e.message.toString())
                }
                tvLunas.visibility = View.GONE
                tvPhoneNumber.text = data.phoneNumber
                data.orderDate?.let { tvOrderDate.text = outputDateFormat.format(inputDateFormat.parse(it))}
            }
        }

        init {
            binding.cardOrder.setOnClickListener {
                onItemClick?.invoke(orderData[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = orderData[position]
        holder.bind(data)
    }

    override fun getItemCount() = orderData.size
}