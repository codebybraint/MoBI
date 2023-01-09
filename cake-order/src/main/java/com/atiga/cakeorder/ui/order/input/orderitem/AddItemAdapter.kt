package com.atiga.cakeorder.ui.order.input.orderitem

import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.product.Product
import com.atiga.cakeorder.databinding.ItemProductBinding
import com.bumptech.glide.Glide
import java.util.*

class AddItemAdapter() : RecyclerView.Adapter<AddItemAdapter.ViewHolder>(), Filterable {
    var onItemClick: ((Product) -> Unit)? = null
    var productFilterList = mutableListOf<Product>()
    var selectedCategoryId: Int? = null
    var searchQuery: String? = null
    private var productData = mutableListOf<Product>()

    fun setData(data: MutableList<Product>) {
        productData = data
    }

    init {
        productFilterList = productData
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemProductBinding.bind(itemView)
        fun bind(data: Product) {
            with(binding) {
                tvName.text = data.name
                tvProductDesc.text = data.description
                if (data.isDecorate) {
                    tvDecorate.text = "Dekorasi"
                    tvDecorate.setBackgroundResource(R.drawable.bg_gradient_1)
                } else {
                    tvDecorate.text = "Non Dekorasi"
                    tvDecorate.setBackgroundResource(R.drawable.bg_gradient_2)
                }
                try{
                    //disini error
                    val imageByteArray = Base64.decode(data.gambarProduk, Base64.DEFAULT)
                    Glide.with(itemView)
                        .load(imageByteArray)
                        .placeholder(R.drawable.default_cake)
                        .centerCrop()
                        .into(imageView8)
                }catch (e: Exception){
                    Log.e("ERROR_CODE",e.toString())
                }
            }
        }

        init {
            binding.cardProduct.setOnClickListener {
                onItemClick?.invoke(productFilterList[adapterPosition])
            }
        }
    }

    fun setCategory(categoryId: Int) {
        selectedCategoryId = categoryId
        val filterByCategory = arrayListOf<Product>()
        filterByCategory.addAll(productData.filter { p -> p.idSubCategory == selectedCategoryId })
        productFilterList = filterByCategory

        searchQuery?.let {
            val resultList = arrayListOf<Product>()
            for (data in filterByCategory) {
                if (data.name.toLowerCase(Locale.ROOT).contains(it.toLowerCase(Locale.ROOT))) {
                    resultList.add(data)
                }
            }
            productFilterList = resultList
        }

        notifyDataSetChanged()
    }

    fun setDefaultCategory() {
        val filterByCategory = arrayListOf<Product>()
        filterByCategory.addAll(productData.filter { p -> p.idSubCategory == selectedCategoryId })
        productFilterList = filterByCategory
        selectedCategoryId = null

        searchQuery?.let {
            val resultList = arrayListOf<Product>()
            for (data in productData) {
                if (data.name.toLowerCase(Locale.ROOT).contains(it.toLowerCase(Locale.ROOT))) {
                    resultList.add(data)
                }
            }
            productFilterList = resultList
        } ?: run {
            productFilterList = productData
        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = productFilterList[position]
        holder.bind(data)
    }

    override fun getItemCount() = productFilterList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(query: CharSequence?): FilterResults {
                searchQuery = query.toString()
                val search = query.toString()
                val filterByCategory = arrayListOf<Product>()

                selectedCategoryId?.let {
                    filterByCategory.addAll(productData.filter { p -> p.idSubCategory == selectedCategoryId })
                }

                if (search.isEmpty()) {
                    productFilterList = if (filterByCategory.isNotEmpty()) {
                        filterByCategory
                    } else {
                        productData
                    }
                } else {
                    val resultList = arrayListOf<Product>()

                    if (filterByCategory.isNotEmpty()) {
                        for (data in filterByCategory) {
                            if (data.name.toLowerCase(Locale.ROOT)
                                    .contains(search.toLowerCase(Locale.ROOT))
                            ) {
                                resultList.add(data)
                            }
                        }
                        productFilterList = resultList
                    } else {
                        for (data in productData) {
                            if (data.name.toLowerCase(Locale.ROOT)
                                    .contains(search.toLowerCase(Locale.ROOT))
                            ) {
                                resultList.add(data)
                            }
                        }
                        productFilterList = resultList
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = productFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(query: CharSequence?, results: FilterResults?) {
                productFilterList = results?.values as ArrayList<Product>
                notifyDataSetChanged()
            }
        }
    }
}