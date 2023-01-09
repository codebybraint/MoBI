package com.atiga.cakeorder.ui.masterdata.product

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.category.Category
import com.atiga.cakeorder.core.domain.model.product.Product
import com.atiga.cakeorder.core.domain.model.subcategory.SubCategory
import com.atiga.cakeorder.core.utils.Status
import com.atiga.cakeorder.databinding.ActivityListProductFromHomeBinding
import com.atiga.cakeorder.databinding.BottomSheetFilterProductBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class ListProductFromHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListProductFromHomeBinding
    private val listProductFromHomeViewModel: ListProductFromHomeViewModel by viewModel()
    private val listProductAdapter = ListProductAdapter()
    private var selectedSubCategoryId = 0
    private var selectedCategoryId = 0
    private var tempSelectedSubCategoryId = 0
    private var tempSelectedCategoryId = 0
    private var products = mutableListOf<Product>()
    private var productList = ArrayList<Product>()
    private var subCategories = mutableListOf<SubCategory>()
    private var categories = mutableListOf<Category>()
    private lateinit var searchView: SearchView
    private lateinit var dialog: BottomSheetDialog

    companion object {
        const val ADD_PRODUCT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListProductFromHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Produk"

        fetchProductData()

        binding.chipFilter.setOnClickListener {
            showBottomSheetDialog()
        }

        binding.btnAddProduct.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivityForResult(intent, ListProductActivity.ADD_PRODUCT)
        }
    }

    private fun fetchProductData() {
        //reset filter
        selectedCategoryId = -1
        selectedSubCategoryId = -1
        binding.tvFilteredBy.visibility = View.GONE

        lifecycleScope.launch {
            listProductFromHomeViewModel.getAllProduct()
        }

        listProductFromHomeViewModel.products.observe(this, {
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

                        with(binding.rvListProduct) {
                            layoutManager = GridLayoutManager(this@ListProductFromHomeActivity, 2)
                            setHasFixedSize(true)
                            adapter = listProductAdapter

                            listProductAdapter.onItemClick = { item ->
                                val intent = Intent(
                                    this@ListProductFromHomeActivity,
                                    AddProductActivity::class.java
                                ).putExtra("productData", item)
                                startActivityForResult(intent, ADD_PRODUCT)
                            }
                        }

                        listProductAdapter.showData()

                        isLoading(false)
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

    private fun showBottomSheetDialog() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_filter_product, null)
        val bottomSheetFilterProductBinding = BottomSheetFilterProductBinding.inflate(
            layoutInflater,
            bottomSheetView as ViewGroup,
            false
        )
        dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetFilterProductBinding.root)

        lifecycleScope.launch {
            listProductFromHomeViewModel.getCategories()
        }

        listProductFromHomeViewModel.category.observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        bottomSheetFilterProductBinding.chipgroupCategory.removeAllViews()

                        categories.clear()
                        it.data?.forEach { category ->
                            categories.add(category)

                            val chip = Chip(this)
                            chip.text = category.name
                            chip.id = category.id
                            chip.isCheckable = true
                            chip.isClickable = true

                            bottomSheetFilterProductBinding.chipgroupCategory.addView(chip)
                        }

                        if (selectedCategoryId > 0) {
                            bottomSheetFilterProductBinding.chipgroupCategory.check(
                                selectedCategoryId
                            )
                            loadSubCategory(bottomSheetFilterProductBinding, selectedCategoryId)
                        }
                    }
                    Status.ERROR -> {
                        it.message?.let { msg -> showMessage(msg) }
                    }
                    Status.LOADING -> {

                    }
                }
            }
        })

        bottomSheetFilterProductBinding.chipgroupCategory.setOnCheckedChangeListener { _, checkedId ->
            tempSelectedCategoryId = checkedId
            tempSelectedSubCategoryId = -1
            if (checkedId < 1) {
                bottomSheetFilterProductBinding.btnFilterProduct.visibility = View.GONE
                bottomSheetFilterProductBinding.tvSubcategory.visibility = View.GONE
            } else {
                loadSubCategory(bottomSheetFilterProductBinding, checkedId)
                bottomSheetFilterProductBinding.btnFilterProduct.visibility = View.VISIBLE
            }
        }

        bottomSheetFilterProductBinding.btnFilterProduct.setOnClickListener {
            if (tempSelectedCategoryId > 0) {
                if (tempSelectedSubCategoryId > 0) {
                    filterData(tempSelectedCategoryId, tempSelectedSubCategoryId)
                } else {
                    filterData(tempSelectedCategoryId, -1)
                    selectedSubCategoryId = -1
                }
            }
        }

        if (selectedCategoryId > 0 || selectedSubCategoryId > 0) {
            bottomSheetFilterProductBinding.btnRemoveFilter.visibility = View.VISIBLE
            bottomSheetFilterProductBinding.btnRemoveFilter.setOnClickListener {
                selectedCategoryId = -1
                selectedSubCategoryId = -1
                binding.tvFilteredBy.visibility = View.GONE

                products = productList.toMutableList()
                listProductAdapter.setData(products)
                listProductAdapter.showData()
                listProductAdapter.notifyItemInserted(products.size - 1)

                binding.rvListProduct.visibility = View.VISIBLE
                binding.tvProductEmpty.visibility = View.GONE
                binding.imgProductEmpty.visibility = View.GONE

                dialog.dismiss()
            }
        }

        if(bottomSheetFilterProductBinding.chipgroupSubcategory.childCount>0){
            bottomSheetFilterProductBinding.chipgroupSubcategory.check(selectedSubCategoryId)
        }

        dialog.show()
    }

    private fun loadSubCategory(binding: BottomSheetFilterProductBinding, id: Int) {
        lifecycleScope.launch {
            listProductFromHomeViewModel.getSubCategoryById(id)
        }

        listProductFromHomeViewModel.subCategory.observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.tvSubcategory.visibility = View.VISIBLE
                        binding.chipgroupSubcategory.removeAllViews()

                        subCategories.clear()
                        it.data?.forEach { subCategory ->
                            subCategories.add(subCategory)

                            val chip = Chip(this)
                            chip.text = subCategory.name
                            chip.id = subCategory.id
                            chip.isCheckable = true
                            chip.isClickable = true
                            if(subCategory.id == selectedSubCategoryId){
                                chip.isChecked = true
                            }

                            binding.chipgroupSubcategory.addView(chip)
                        }
                    }
                    Status.ERROR -> {
                        it.message?.let { msg -> showMessage(msg) }
                    }
                    Status.LOADING -> {

                    }
                }
            }
        })

        binding.chipgroupSubcategory.setOnCheckedChangeListener { _, checkedId ->
            tempSelectedSubCategoryId = checkedId
        }

    }

    private fun filterData(categoryId: Int, subCategoryId: Int) {
        val filteredList = arrayListOf<Product>()
        for (p in productList) {
            if (p.idKategori == categoryId) {
                if (subCategoryId > 0) {
                    if (p.idSubCategory == subCategoryId) {
                        filteredList.add(p)
                    }
                } else {
                    filteredList.add(p)
                }
            }
        }

        listProductAdapter.setData(filteredList)
        listProductAdapter.showData()
        listProductAdapter.notifyItemInserted(filteredList.size - 1)

        var textCategory = ""
        var textSubCategory = ""
        if (categoryId > 0) {
            textCategory = categories.first { c -> c.id == categoryId }.name
            binding.tvFilteredBy.text = textCategory
        }
        if (subCategoryId > 0) {
            textSubCategory = subCategories.first { s -> s.id == subCategoryId }.name

            binding.tvFilteredBy.text = "$textCategory - $textSubCategory"
        }

        binding.tvFilteredBy.visibility = View.VISIBLE

        if (categoryId > 0) selectedCategoryId = categoryId
        if (subCategoryId > 0) selectedSubCategoryId = subCategoryId

        if (filteredList.size < 1) {
            binding.rvListProduct.visibility = View.INVISIBLE
            binding.tvProductEmpty.visibility = View.VISIBLE
            binding.imgProductEmpty.visibility = View.VISIBLE
        } else {
            binding.rvListProduct.visibility = View.VISIBLE
            binding.tvProductEmpty.visibility = View.GONE
            binding.imgProductEmpty.visibility = View.GONE
        }

        dialog.dismiss()
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(bool: Boolean) {
        if (bool) {
            binding.rvListProduct.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.rvListProduct.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_item, menu)

        val searchItem: MenuItem = menu.findItem(R.id.action_search)
        searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchView.setOnCloseListener { true }

        val searchPlate =
            searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
        searchPlate.hint = "Cari produk"
        val searchPlateView: View = searchView.findViewById(androidx.appcompat.R.id.search_plate)
        searchPlateView.setBackgroundColor(
            ContextCompat.getColor(
                this,
                android.R.color.transparent
            )
        )

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                listProductAdapter.filter.filter(newText)
                return false
            }
        })

        val searchManager =
            getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        return super.onCreateOptionsMenu(menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PRODUCT && resultCode == Activity.RESULT_OK) {
            fetchProductData()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (!searchView.isIconified) {
            searchView.onActionViewCollapsed()
        } else {
            super.onBackPressed()
        }
    }
}