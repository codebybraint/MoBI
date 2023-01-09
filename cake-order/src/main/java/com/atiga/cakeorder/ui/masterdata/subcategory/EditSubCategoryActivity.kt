package com.atiga.cakeorder.ui.masterdata.subcategory

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.network.response.EditSubCategoryResponse
import com.atiga.cakeorder.core.utils.Status
import com.atiga.cakeorder.databinding.ActivityEditSubCategoryBinding
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class EditSubCategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditSubCategoryBinding
    private val editSubCategoryViewModel: EditSubCategoryViewModel by viewModel()
    private var subCategoryId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditSubCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Sub kategori"

        fetchData()

        editSubCategoryViewModel.category.observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        isLoading(stat = false, isDelete = false)
                    }
                    Status.ERROR -> {
                        isLoading(stat = false, isDelete = false)
                        it.message?.let { msg ->
                            showMessage(msg)
                            Log.e("cek", "onCreate: $msg")
                        }
                    }
                    Status.LOADING -> {
                        isLoading(stat = true, isDelete = false)
                    }
                }
            }
        })

        editSubCategoryViewModel.detailSubCategory.observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.etSubcategory.setText(it.data?.name)

                        binding.btnDeleteSubcategory.setOnClickListener { _ ->
                            lifecycleScope.launch {
                                it.data?.id?.let { id -> showAlertDialog(id) }
                            }
                        }

                        isLoading(stat = false, isDelete = false)
                    }
                    Status.ERROR -> {
                        isLoading(stat = false, isDelete = false)
                        it.message?.let { msg ->
                            showMessage(msg)
                            Log.e("cek", "onCreate: $msg")
                        }
                    }
                    Status.LOADING -> {
                        isLoading(stat = true, isDelete = false)
                    }
                }
            }
        })

        editSubCategoryViewModel.isDeleted.observe(this, {
            it?.let {
                isLoading(stat = false, isDelete = true)
                showMessage("Sub kategori berhasil dihapus")
                val returnIntent = Intent()
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        })

        editSubCategoryViewModel.error.observe(this, {
            it?.let {
                showMessage(it)
            }
        })
    }

    private fun fetchData() {
        subCategoryId = intent.getIntExtra("id", 0)
        val categoryId = intent.getIntExtra("categoryId", 0)
        lifecycleScope.launch {
            editSubCategoryViewModel.getCategory(categoryId)
            editSubCategoryViewModel.getSubCategoryDetail(subCategoryId)
        }
    }

    private fun showAlertDialog(id: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.dialog_title_warning)
        builder.setMessage(R.string.dialog_delete_subcategory_message)
        builder.setPositiveButton(R.string.dialog_backpressed_yes) { dialog, _ ->
            isLoading(stat = true, isDelete = true)
            lifecycleScope.launch {
                editSubCategoryViewModel.deleteSubCategory(id)
            }
        }

        builder.setNegativeButton(R.string.dialog_backpressed_no) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun isLoading(stat: Boolean, isDelete: Boolean) {
        if (stat) {
            binding.progressBarUpdate.visibility = View.VISIBLE
            if (isDelete) {
                binding.btnDeleteSubcategory.isEnabled = false
            }
        } else {
            binding.progressBarUpdate.visibility = View.GONE
            if (isDelete) {
                binding.btnDeleteSubcategory.isEnabled = true
            }
        }
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
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
            if (binding.etSubcategory.text.isNotBlank()) {
                item.isEnabled = false
                isLoading(stat = true, isDelete = false)

                lifecycleScope.launch {
                    editSubCategoryViewModel.updateSubCategory(
                        subCategoryId,
                        EditSubCategoryResponse(
                            subCategoryId,
                            binding.etSubcategory.text.toString()
                        )
                    )
                }

                editSubCategoryViewModel.editedSubCategory.observe(this, {
                    it?.let {
                        isLoading(stat = false, isDelete = false)
                        Toast.makeText(this, "Sub kategori berhasil disimpan", Toast.LENGTH_SHORT).show()
                        val returnIntent = Intent()
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }
                })
            } else {
                showMessage("Nama sub kategori tidak boleh kosong")
            }
        }
        return super.onOptionsItemSelected(item)
    }

}