package com.atiga.cakeorder.ui.masterdata.subcategory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.subcategory.SubCategory
import com.atiga.cakeorder.databinding.ItemAddedSubcategoryBinding

class AddSubCategoryAdapter : RecyclerView.Adapter<AddSubCategoryAdapter.ViewHolder>() {
    var onItemClick: ((SubCategory) -> Unit)? = null
    var subCategoryData = mutableListOf<SubCategory>()

    fun setData(data: MutableList<SubCategory>) {
        subCategoryData = data
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemAddedSubcategoryBinding.bind(itemView)
        fun bind(data: SubCategory) {
            with(binding) {
                tvSubcategory.text = data.name
            }
        }

        init {
            binding.cardAddedSubcategory.setOnClickListener {
                onItemClick?.invoke(subCategoryData[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_added_subcategory, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = subCategoryData[position]
        holder.bind(data)
    }

    override fun getItemCount() = subCategoryData.size
}