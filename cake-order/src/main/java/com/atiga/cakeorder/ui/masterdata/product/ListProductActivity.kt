package com.atiga.cakeorder.ui.masterdata.product

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.atiga.cakeorder.core.domain.model.product.Product
import com.atiga.cakeorder.core.utils.Status
import com.atiga.cakeorder.databinding.ActivityListProductBinding
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class ListProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListProductBinding
    private val listProductViewModel: ListProductViewModel by viewModel()
    private val listProductAdapter = ListProductAdapter()
    private var subCategoryId = 0
    private var products = mutableListOf<Product>()
    private var productList = ArrayList<Product>()

    companion object {
        const val ADD_PRODUCT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        subCategoryId = intent.getIntExtra("id", 0)
        lifecycleScope.launch {
            listProductViewModel.getSubCategory(subCategoryId)
        }

        fetchProductData(subCategoryId)

        listProductViewModel.subCategory.observe(this, {
            it?.let {
                supportActionBar?.title = it.data?.name
            }
        })

        binding.btnAddProduct.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java).putExtra("id", subCategoryId)
            startActivityForResult(intent, ADD_PRODUCT)
        }
    }

    private fun fetchProductData(id: Int){
        lifecycleScope.launch {
            listProductViewModel.getProduct(id)
        }

        listProductViewModel.products.observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        products.clear()
                        productList.clear()

                        it.data?.map { result ->
                            productList.add(result)
                        }

                        products = productList.toMutableList()
                        listProductAdapter.setData(products)

                        with(binding.rvAddedProduct) {
                            layoutManager = GridLayoutManager(this@ListProductActivity, 2)
                            setHasFixedSize(true)
                            adapter = listProductAdapter

                            listProductAdapter.onItemClick = { item ->
                                val intent = Intent(
                                    this@ListProductActivity,
                                    AddProductActivity::class.java
                                ).putExtra("productData", item)
                                startActivityForResult(intent, ADD_PRODUCT)
                            }
                        }

                        listProductAdapter.showData()

                        isLoading(false)
                        Log.d("cekprd", "fetchProductData: size ${products.size}")
                    }
                    Status.ERROR -> {
                        isLoading(false)
                        it.message?.let { msg -> showMessage(msg) }
                    }
                    Status.LOADING -> {
                        isLoading(true)
                    }
                }
            }
        })
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(bool: Boolean) {
        if (bool) {
            binding.rvAddedProduct.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.rvAddedProduct.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_PRODUCT && resultCode == Activity.RESULT_OK){
            fetchProductData(subCategoryId)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}