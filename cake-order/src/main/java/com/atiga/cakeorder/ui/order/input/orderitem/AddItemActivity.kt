package com.atiga.cakeorder.ui.order.input.orderitem

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.order.OrderAddItem
import com.atiga.cakeorder.core.domain.model.order.OrderAddItemDetail
import com.atiga.cakeorder.core.domain.model.product.Product
import com.atiga.cakeorder.core.utils.Status
import com.atiga.cakeorder.databinding.ActivityAddItemBinding
import com.atiga.cakeorder.databinding.BottomSheetAddProductBinding
import com.atiga.cakeorder.ui.order.input.InputOrderActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class AddItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddItemBinding
    private val addItemViewModel: AddItemViewModel by viewModel()
    private lateinit var searchView : SearchView
    private var addItemAdapter = AddItemAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Pilih produk"

        fetchData()

    }

    private fun fetchData(){
        lifecycleScope.launch {
            addItemViewModel.getCategories()
            addItemViewModel.getAllProduct()
        }

        addItemViewModel.products.observe(this, {
            it?.let { resource ->
                when(resource.status){
                    Status.SUCCESS -> {
                        val listProduct = mutableListOf<Product>()

                        it.data?.map { result ->
                            listProduct.add(result)
                        }

                        addItemAdapter.setData(listProduct)

                        with(binding.rvProduct){
                            layoutManager = GridLayoutManager(this@AddItemActivity, 2)
                            setHasFixedSize(true)
                            adapter = addItemAdapter
                        }

                        addItemAdapter.onItemClick = {
                            hideKeyboard()
                            showBottomSheetDialog(it)
                        }

                        // to show all products without any filter
                        addItemAdapter.setDefaultCategory()

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

    private fun showMessage(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(stat: Boolean){
        if(stat){
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showBottomSheetDialog(data: Product){
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_add_product, null)
        val binding = BottomSheetAddProductBinding.inflate(layoutInflater, bottomSheetView as ViewGroup, false)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(binding.root)
        bottomSheetView.setOnClickListener { dialog.dismiss() }

        if(!data.gambarProduk.isNullOrBlank()){
            val imageByteArray = Base64.decode(data.gambarProduk, Base64.DEFAULT)
            Glide.with(this).load(imageByteArray).centerCrop().into(binding.imageView9)
        } else {
            Glide.with(this).load(R.drawable.default_cake).into(binding.imageView9)
        }

        binding.tvName.text = data.name
        binding.tvDesc.text = data.description
        binding.btnAmountMin.setOnClickListener {
            if(binding.tvAmount.text != "1"){
                binding.tvAmount.text = (binding.tvAmount.text.toString().toInt() - 1).toString()
            }
        }
        binding.btnAmountPlus.setOnClickListener {
            binding.tvAmount.text = (binding.tvAmount.text.toString().toInt() + 1).toString()
        }
        binding.btnAdd.setOnClickListener {
            data.addAmount = binding.tvAmount.text.toString().toInt()
            val returnIntent = Intent().putExtra("result", data)
            setResult(Activity.RESULT_OK, returnIntent)
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_item, menu)

        val searchItem: MenuItem = menu.findItem(R.id.action_search)
        searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchView.setOnCloseListener { true }

        val searchPlate = searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
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
                addItemAdapter.filter.filter(newText)
                return false
            }
        })

        val searchManager =
            getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
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