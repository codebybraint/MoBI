package com.atiga.cakeorder.ui.capacity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.subcategory.SubCategory
import com.atiga.cakeorder.databinding.ItemCapacityBinding

class CapacityAdapter: RecyclerView.Adapter<CapacityAdapter.ViewHolder>(){
    var onItemClick: ((SubCategory) -> Unit)? = null
    var subCategoryData = mutableListOf<SubCategory>()

    fun setData(data: MutableList<SubCategory>) {
        subCategoryData = data
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemCapacityBinding.bind(itemView)
        fun bind(data: SubCategory){
            with(binding){
                tvSubCategory.text = data.name
                tvAmount.text = data.maxOrder.toString()
            }
        }
        init {
            binding.btnAdd.setOnClickListener {
                onItemClick?.invoke(subCategoryData[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_capacity, parent, false)
        )

    override fun onBindViewHolder(holder: CapacityAdapter.ViewHolder, position: Int) {
        val data = subCategoryData[position]
        holder.bind(data)
    }

    override fun getItemCount() = subCategoryData.size

}