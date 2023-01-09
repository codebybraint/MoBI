package com.atiga.cakeorder.ui.masterdata.subcategory

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.subcategory.AddSubCategory
import com.atiga.cakeorder.core.domain.model.subcategory.SubCategory
import com.atiga.cakeorder.core.utils.Status
import com.atiga.cakeorder.databinding.ActivityAddSubCategoryBinding
import com.atiga.cakeorder.databinding.BottomSheetAddSubcategoryBinding
import com.atiga.cakeorder.databinding.BottomSheetSubcategoryMenuBinding
import com.atiga.cakeorder.ui.masterdata.product.ListProductActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class AddSubCategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddSubCategoryBinding
    private val addSubCategoryViewModel: AddSubCategoryViewModel by viewModel()
    private var subCategories = mutableListOf<SubCategory>()
    private val addSubCategoryAdapter = AddSubCategoryAdapter()
    private var categoryId = 0

    companion object {
        const val EDIT_SUBCATEGORY = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSubCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        categoryId = intent.getIntExtra("id", 0)
        lifecycleScope.launch {
            addSubCategoryViewModel.getCategoryDetail(categoryId)
        }

        fetchSubCategoryData(categoryId)

        addSubCategoryViewModel.categoryDetail.observe(this, {
            it?.let {
                supportActionBar?.title = it.data?.name
            }
        })

        binding.btnAddSubcategory.setOnClickListener {
            showBottomSheetDialogAddSubCategory(
                categoryId,
                addSubCategoryAdapter
            )
        }

        addSubCategoryViewModel.error.observe(this, {
            it?.let {
                showMessage(it)
            }
        })
    }

    private fun fetchSubCategoryData(id: Int) {
        lifecycleScope.launch {
            addSubCategoryViewModel.getSubCategoryById(id)
        }

        addSubCategoryViewModel.subCategoryData.observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        subCategories.clear()

                        it.data?.map { result ->
                            subCategories.add(result)
                        }

                        addSubCategoryAdapter.setData(subCategories)

                        with(binding.rvSubcategory) {
                            layoutManager = LinearLayoutManager(
                                this@AddSubCategoryActivity,
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                            setHasFixedSize(true)
                            adapter = addSubCategoryAdapter

                            addSubCategoryAdapter.onItemClick = { item ->
                                showBottomSheetDialogMenu(item.id)
                            }
                        }

                        addSubCategoryAdapter.notifyItemInserted(subCategories.size - 1)

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

    private fun showBottomSheetDialogAddSubCategory(
        categoryId: Int,
        adapter: AddSubCategoryAdapter?
    ) {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_add_subcategory, null)
        val binding = BottomSheetAddSubcategoryBinding.inflate(
            layoutInflater,
            bottomSheetView as ViewGroup,
            false
        )
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(binding.root)

        binding.btnSave.setOnClickListener {

            if (binding.etSubcategory.text.isNotEmpty()) {
                binding.btnSave.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                lifecycleScope.launch {
                    addSubCategoryViewModel.addSubCategory(
                        categoryId,
                        AddSubCategory(binding.etSubcategory.text.toString())
                    )
                }
                addSubCategoryViewModel.addedSubCategory.observe(this, {
                    it?.let {
                        Log.d("cek", "showBottomSheetDialogAddSubCategory: add subcategory")
//                        showMessage("Sub kategori berhasil ditambahkan")
                        fetchSubCategoryData(categoryId)
                    }
                })
            } else {
                showMessage("Nama sub kategori tidak boleh kosong")
            }

            adapter?.notifyDataSetChanged()
            adapter?.notifyItemInserted(subCategories.size - 1)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showBottomSheetDialogMenu(subCategoryId: Int) {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_category_menu, null)
        val binding = BottomSheetSubcategoryMenuBinding.inflate(
            layoutInflater,
            bottomSheetView as ViewGroup,
            false
        )
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(binding.root)

        binding.btnEditSubcategory.setOnClickListener {
            val intent =
                Intent(this, EditSubCategoryActivity::class.java).putExtra("id", subCategoryId).putExtra("categoryId", categoryId)
            startActivityForResult(intent, EDIT_SUBCATEGORY)
            dialog.dismiss()
        }

        binding.btnAddProduct.setOnClickListener {
            val intent = Intent(this, ListProductActivity::class.java).putExtra("id", subCategoryId)
            startActivity(intent)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun isLoading(bool: Boolean) {
        if (bool) {
            binding.rvSubcategory.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.rvSubcategory.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_SUBCATEGORY && resultCode == Activity.RESULT_OK) {
            fetchSubCategoryData(categoryId)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}