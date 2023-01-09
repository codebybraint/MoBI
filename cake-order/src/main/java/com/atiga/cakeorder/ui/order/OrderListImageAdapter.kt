package com.atiga.cakeorder.ui.order

import android.util.Base64
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
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule

@GlideModule
class OrderListImageAdapter:  RecyclerView.Adapter<OrderListImageAdapter.ViewHolder>(){
    var listOrderImage = listOf<ItemOrder>()

    fun setData(data: List<ItemOrder>) {
        listOrderImage = data
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemOrderImagesBinding.bind(itemView)
        fun bind(data: ItemOrder) {
            with(binding) {

                data.images?.let { listImages ->
                    try{
                       if (listImages.isNotEmpty()) {
                        val imageByteArray = Base64.decode(listImages[0], Base64.DEFAULT)
                        val image1 = listImages[0]
                        Glide.with(itemView)
                            .load(imageByteArray)
                            .centerCrop()
                            .into(binding.img1)
                        binding.img1.visibility = View.VISIBLE
                        if (listImages.size >= 2) {
                            val image2 = listImages[0]
                            Glide.with(itemView)
                                .load(imageByteArray)
                                .centerCrop()
                                .into(binding.img2)
                            binding.img2.visibility = View.VISIBLE
                        }
                        if (listImages.size == 3) {
                            val image2 = listImages[0]
                            Glide.with(itemView)
                                .load(imageByteArray)
                                .centerCrop()
                                .into(binding.img3)
                            binding.img3.visibility = View.VISIBLE
                        }
                           binding.scrollImageOrder.visibility = View.VISIBLE
                    }
                       else {
                        binding.cardOrderImages.visibility = View.GONE
                    }
                    } catch (e: Exception){
                        Log.e("ERROR_CODE", e.message.toString())
                    }
                } ?: run{
                    binding.scrollImageOrder.visibility = View.GONE
                }
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_order_images, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listOrderImage[position]
        holder.bind(data)
    }

    override fun getItemCount() = listOrderImage.size
}