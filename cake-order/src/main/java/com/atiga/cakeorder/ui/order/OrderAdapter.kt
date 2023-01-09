package com.atiga.cakeorder.ui.order

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.order.ItemOrder
import com.atiga.cakeorder.core.domain.model.order.Order
import com.atiga.cakeorder.core.domain.model.order.OrderAddItemDetail
import com.atiga.cakeorder.core.domain.model.product.Product
import com.atiga.cakeorder.databinding.ItemOrderBinding
import com.atiga.cakeorder.databinding.ItemOrderItemDetailCardBinding
import com.atiga.cakeorder.databinding.ItemOrderProductBinding
import com.atiga.cakeorder.databinding.OrderFragmentBinding
import com.atiga.cakeorder.ui.order.detail.DetailOrderViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter: RecyclerView.Adapter<OrderAdapter.ViewHolder>() , Filterable {
    var onItemClick: ((String) -> Unit)? = null
    var onLongItemClick: ((String) -> Unit)? = null
    var orderData = mutableListOf<Order>()
    var orderFiltered = mutableListOf<Order>()
    var orderListProduct = mutableListOf<ItemOrder>()
    var orderListProductAdapter = OrderListProductAdapter()
    var orderListImageAdapter = OrderListImageAdapter()
    var searchQuery: String? = null

    fun setData(data: MutableList<Order>) {
//        orderData.clear()
        orderData = data
    }

    init{
        orderFiltered = orderData
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemOrderBinding.bind(itemView)
        private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        private val outputDateFormat = SimpleDateFormat("d MMM yyyy")

        fun bind(data: Order) {
            with(binding) {

                tvOrderNumber.text = data.orderNumber
                tvCustomer.text = data.customerName
                data.orderDate?.let { tvOrderDate.text = outputDateFormat.format(inputDateFormat.parse(it))}
                tvPhoneNumber.text = data.phoneNumber
                orderListProduct = data.items.toMutableList()
                try{
                    orderListProductAdapter.setData(orderListProduct)
                    with(binding.rvOrderitemProduct){
                        layoutManager = LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
                        setHasFixedSize(true)
                        adapter = orderListProductAdapter
                    }
                }catch (e: Exception){
                    Log.e("ERROR_CODE", e.message.toString())
                }
                try{
                    orderListImageAdapter.setData(orderListProduct)
                    with(binding.rvOrderitemImage){
                        layoutManager = LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
                        setHasFixedSize(true)
                        adapter = orderListImageAdapter
                    }
                }catch (e: Exception){
                    Log.e("ERROR_CODE", e.message.toString())
                }
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
                orderFiltered[adapterPosition].orderNumber?.let { id -> onItemClick?.invoke(id) }
            }

            binding.cardOrder.setOnLongClickListener {
                orderFiltered[adapterPosition].orderNumber?.let { id -> onLongItemClick?.invoke(id) }
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        )

    override fun onBindViewHolder(holder: OrderAdapter.ViewHolder, position: Int) {
        val data = orderFiltered[position]
        holder.bind(data)
    }

    override fun getItemCount() = orderFiltered.size

    // Filter data dari search
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(query: CharSequence?): FilterResults {
                searchQuery = query.toString()
                val search = query.toString()

                // TODO search function disini
                if (search.isEmpty()){
                    orderFiltered = orderData
                } else {
                    val resultList = arrayListOf<Order>()
                    for (data in orderData){
                        if ((data.customerName?.toLowerCase(Locale.ROOT)!!.contains(search.toLowerCase(Locale.ROOT))) ||
                                (data.phoneNumber!!.contains(search))) {
                            resultList.add(data)
                        }
                    }
                    orderFiltered = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = orderFiltered
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(query: CharSequence?, results: FilterResults?) {
                orderFiltered = results?.values as ArrayList<Order>
                notifyDataSetChanged()
            }
        }
    }

    fun showData() {
        searchQuery?.let {
            val resultList = arrayListOf<Order>()
            for (data in orderData) {
                if (data.customerName?.toLowerCase(Locale.ROOT)!!.contains(it.toLowerCase(Locale.ROOT))){
                    resultList.add(data)
                }
            }
            orderFiltered = resultList
        } ?: run {
            orderFiltered = orderData
        }
        notifyDataSetChanged()
    }
}