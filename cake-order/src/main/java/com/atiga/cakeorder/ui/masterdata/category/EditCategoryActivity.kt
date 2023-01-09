package com.atiga.cakeorder.ui.masterdata.category

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.category.AddCategory
import com.atiga.cakeorder.core.domain.model.category.Category
import com.atiga.cakeorder.core.utils.Status
import com.atiga.cakeorder.databinding.ActivityEditCategoryBinding
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class EditCategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditCategoryBinding
    private val editCategoryViewModel: EditCategoryViewModel by viewModel()
    private var isEditCategory = false
    private var categoryId = 0
    private var categoryData: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Kategori"

        fetchData()

        editCategoryViewModel.isDeleted.observe(this, {
            it?.let {
                showMessage("Kategori berhasil dihapus")
                val returnIntent = Intent()
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        })

        editCategoryViewModel.error.observe(this, {
            it?.let {
                showMessage(it)
            }
        })
    }

    private fun fetchData() {
        categoryId = intent.getIntExtra("id", 0)
        if (categoryId != 0) {
            isEditCategory = true
            binding.btnDeleteCategory.visibility = View.VISIBLE

            lifecycleScope.launch {
                editCategoryViewModel.getCategoryDetail(categoryId)
            }

            editCategoryViewModel.detail.observe(this, {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            binding.etCategoryName.setText(it.data?.name)

                            binding.btnDeleteCategory.setOnClickListener { _ ->
                                lifecycleScope.launch {
                                    it.data?.id?.let { id -> showAlertDialog(id) }
                                }
                            }

                            isLoading(stat = false, isDelete = false)
                        }
                        Status.ERROR -> {
                            isLoading(stat = false, isDelete = false)
                            it.message?.let { msg -> showMessage(msg) }
                        }
                        Status.LOADING -> {
                            isLoading(stat = true, isDelete = false)
                        }
                    }
                }
            })
        }
    }

    private fun showAlertDialog(id: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.dialog_title_warning)
        builder.setMessage(R.string.dialog_delete_category_message)
        builder.setPositiveButton(R.string.dialog_backpressed_yes) { dialog, which ->
            isLoading(stat = true, isDelete = true)
            lifecycleScope.launch {
                editCategoryViewModel.deleteCategory(id)
            }
        }

        builder.setNegativeButton(R.string.dialog_backpressed_no) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun isLoading(stat: Boolean, isDelete: Boolean) {
        if (stat) {
            binding.progressBarUpdate.visibility = View.VISIBLE
            if (isDelete) {
                binding.btnDeleteCategory.isEnabled = false
            }
        } else {
            binding.progressBarUpdate.visibility = View.GONE
            if (isDelete) {
                binding.btnDeleteCategory.isEnabled = true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_submit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        } else if (item.itemId == R.id.action_submit) {
            if (isEditCategory && binding.etCategoryName.text.isNotBlank()) {
                item.isEnabled = false
                isLoading(stat = true, isDelete = false)

                lifecycleScope.launch {
                    editCategoryViewModel.updateCategory(
                        categoryId,
                        Category(categoryId, binding.etCategoryName.text.toString())
                    )
                }

                editCategoryViewModel.editedCategory.observe(this, {
                    it?.let {
                        showMessage("Kategori berhasil diubah")
                        val returnIntent = Intent()
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }
                })
            } else if (!isEditCategory && binding.etCategoryName.text.isNotBlank()) {
                item.isEnabled = false
                isLoading(stat = true, isDelete = false)

                lifecycleScope.launchWhenCreated {
                    editCategoryViewModel.addCategory(AddCategory(binding.etCategoryName.text.toString()))
                }

                editCategoryViewModel.addedCategory.observe(this, {
                    it?.let {
                        showMessage("Kategori berhasil ditambahkan")
                        val returnIntent = Intent()
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }
                })
            } else {
                showMessage("Nama kategori tidak boleh kosong")
            }
        }
        return super.onOptionsItemSelected(item)
    }
}