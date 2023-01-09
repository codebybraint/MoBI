package com.atiga.cakeorder.ui.masterdata

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.category.Category
import com.atiga.cakeorder.databinding.ItemCategoryBinding

class MasterDataAdapter: RecyclerView.Adapter<MasterDataAdapter.ViewHolder>() {
    var onItemClick: ((Int) -> Unit)? = null
    var categoryData = mutableListOf<Category>()

    fun setData(data: MutableList<Category>) {
        categoryData = data
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemCategoryBinding.bind(itemView)
        fun bind(data: Category) {
            with(binding) {
                tvName.text = data.name
            }
        }

        init {
            binding.cardCategory.setOnClickListener {
                onItemClick?.invoke(categoryData[adapterPosition].id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        )

    override fun onBindViewHolder(holder: MasterDataAdapter.ViewHolder, position: Int) {
        val data = categoryData[position]
        holder.bind(data)
    }

    override fun getItemCount() = categoryData.size

}