package com.atiga.cakeorder.ui.masterdata

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.category.Category
import com.atiga.cakeorder.core.utils.Status
import com.atiga.cakeorder.databinding.BottomSheetCategoryMenuBinding
import com.atiga.cakeorder.databinding.MasterdataFragmentBinding
import com.atiga.cakeorder.ui.masterdata.category.EditCategoryActivity
import com.atiga.cakeorder.ui.masterdata.product.ListProductFromHomeActivity
import com.atiga.cakeorder.ui.masterdata.subcategory.AddSubCategoryActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class MasterDataFragment : Fragment() {

    private val masterDataViewModel: MasterDataViewModel by viewModel()
    private var _binding: MasterdataFragmentBinding? = null
    private val binding get() = _binding!!
    private val masterDataAdapter = MasterDataAdapter()
    private var categories = mutableListOf<Category>()

    companion object {
        const val ADD_CATEGORY = 1
        const val EDIT_CATEGORY = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MasterdataFragmentBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        binding.fabAddCategory.setOnClickListener { startActivityForResult(Intent(activity, EditCategoryActivity::class.java), ADD_CATEGORY) }

        binding.fabAddCategory.setOnClickListener { startActivityForResult(Intent(activity, EditCategoryActivity::class.java), ADD_CATEGORY) }

        binding.fabAddProduct.setOnClickListener { startActivity(Intent(activity, ListProductFromHomeActivity::class.java)) }

        fetchData()

        masterDataAdapter.onItemClick = {
            showBottomSheetDialog(it)
        }
    }

    private fun fetchData(){
        lifecycleScope.launch {
            masterDataViewModel.getCategories()
        }

        masterDataViewModel.categoryData.observe(viewLifecycleOwner, {
            it?.let { resource ->
                when(resource.status){
                    Status.SUCCESS -> {
                        isLoading(false)

                        categories.clear()

                        it.data?.map { result ->
                            categories.add(result)
                        }

                        masterDataAdapter.setData(categories)

                        with(binding.rvCategory){
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            setHasFixedSize(true)
                            adapter = masterDataAdapter
                        }

                        masterDataAdapter.notifyItemInserted(categories.size-1)

                        if(categories.size < 1){
                            binding.imgOrderEmpty.visibility = View.VISIBLE
                            binding.tvOrderEmpty.visibility = View.VISIBLE
                        } else {
                            binding.imgOrderEmpty.visibility = View.GONE
                            binding.tvOrderEmpty.visibility = View.GONE
                        }
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

    private fun isLoading(bool: Boolean){
        if(bool){
            binding.rvCategory.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.rvCategory.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showBottomSheetDialog(id: Int) {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_category_menu, null)
        val binding = BottomSheetCategoryMenuBinding.inflate(
            layoutInflater,
            bottomSheetView as ViewGroup,
            false
        )
        val dialog = BottomSheetDialog(this.requireContext())
        dialog.setContentView(binding.root)

        binding.btnEditCategory.setOnClickListener {
            val intent = Intent(activity, EditCategoryActivity::class.java).putExtra("id", id)
            startActivityForResult(intent, EDIT_CATEGORY)
            dialog.dismiss()
        }

        binding.btnAddSubcategory.setOnClickListener {
            val intent = Intent(activity, AddSubCategoryActivity::class.java).putExtra("id", id)
            startActivity(intent)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showMessage(msg: String){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_CATEGORY || requestCode == EDIT_CATEGORY && resultCode == Activity.RESULT_OK){
            fetchData()
        }
    }
}