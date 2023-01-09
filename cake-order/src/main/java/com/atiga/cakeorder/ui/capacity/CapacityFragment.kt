package com.atiga.cakeorder.ui.capacity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.subcategory.SubCategory
import com.atiga.cakeorder.core.utils.Status
import com.atiga.cakeorder.databinding.BottomSheetCapacityBinding
import com.atiga.cakeorder.databinding.CapacityFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class CapacityFragment : Fragment() {

    private val capacityViewModel: CapacityViewModel by viewModel()
    private var _binding: CapacityFragmentBinding? = null
    private val binding get() = _binding!!
    private var subCategories = mutableListOf<SubCategory>()
    private val capacityAdapter = CapacityAdapter()
    private lateinit var dialog: BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CapacityFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fetchData()

        capacityAdapter.onItemClick = {
            showBottomSheetDialog(it)
        }

        capacityViewModel.editCapacity.observe(viewLifecycleOwner, {
            it?.let {
                dialog.dismiss()
                showMessage("Kapasitas ${it.name} berhasil diupdate")
                fetchData()
            }
        })

        capacityViewModel.error.observe(viewLifecycleOwner, {
            it?.let{
                showMessage(it)
            }
        })
    }

    private fun fetchData(){
        lifecycleScope.launch {
            capacityViewModel.getAllSubCategory()
        }

        capacityViewModel.subCategoryData.observe(viewLifecycleOwner, {
            it?.let { resource ->
                when(resource.status){
                    Status.SUCCESS -> {
                        isLoading(false)

                        subCategories.clear()

                        it.data?.map { result ->
                            subCategories.add(result)
                        }

                        capacityAdapter.setData(subCategories)

                        with(binding.rvCapacity){
                            layoutManager = LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
                            setHasFixedSize(true)
                            adapter = capacityAdapter
                        }

                        capacityAdapter.notifyDataSetChanged()

                        if(subCategories.size < 1){
                            binding.imgOrderEmpty.visibility = View.VISIBLE
                            binding.tvOrderEmpty.visibility = View.VISIBLE
                        } else {
                            binding.imgOrderEmpty.visibility = View.GONE
                            binding.tvOrderEmpty.visibility = View.GONE
                        }
                    }
                    Status.ERROR -> {
                        it.message?.let { msg -> showMessage(msg) }
                    }
                    Status.LOADING -> {
                        isLoading(true)
                    }
                }
            }
        })
    }

    private fun showBottomSheetDialog(data: SubCategory){
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_capacity, null)
        val binding = BottomSheetCapacityBinding.inflate(layoutInflater, bottomSheetView as ViewGroup, false)
        dialog = BottomSheetDialog(this.requireContext())
        dialog.setContentView(binding.root)
        bottomSheetView.setOnClickListener { dialog.dismiss() }

        binding.tvSubCategory.text = data.name
        binding.tvCurrentAmount.text = data.maxOrder.toString()
//        binding.tvTotalCapacity.text = data.maxOrder.toString()

        binding.btnAddCapacity.setOnClickListener {
            if(!binding.etAmount.text.toString().isNullOrBlank()){
                binding.btnAddCapacity.isEnabled = false
                lifecycleScope.launch {
                    capacityViewModel.updateCapacity(data.id, binding.etAmount.text.toString().toInt())
                }
                isLoading(true)
            } else {
                dialog.dismiss()
            }
        }

        binding.etAmount.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    if(!s.isNullOrBlank()){
                        var existing = data.maxOrder.toString().toInt()
                        var add = binding.etAmount.text.toString().toInt()
//                        binding.tvTotalCapacity.text = (existing + add).toString()
                    }
                } catch (e: Exception){

                }
            }

            override fun afterTextChanged(p0: Editable?) {
                //
            }

        })

        dialog.show()

    }

    private fun isLoading(bool: Boolean){
        if(bool){
            binding.rvCapacity.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.rvCapacity.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showMessage(msg: String){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

}