package com.atiga.cakeorder.ui.report.detail

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.enum.ReportType
import com.atiga.cakeorder.core.domain.model.category.SpinnerCategory
import com.atiga.cakeorder.core.domain.model.order.Order
import com.atiga.cakeorder.core.domain.model.report.Report
import com.atiga.cakeorder.databinding.ActivityDetailReportBinding
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class DetailReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailReportBinding
    private val detailReportViewModel: DetailReportViewModel by viewModel()
    private var reportType = 0
    private var selectedCategoryId = 0
    private var selectedSubCategoryId = 0
    private lateinit var categoryAdapter: ArrayAdapter<SpinnerCategory>
    private lateinit var subCategoryAdapter: ArrayAdapter<SpinnerCategory>
    private val detailReportAdapter = DetailReportAdapter()
    private var orders = mutableListOf<Order>()
    private val todayDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val outputDateFormat = SimpleDateFormat("d MMM yyyy")
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val todayDate = outputDateFormat.format(inputDateFormat.parse(LocalDateTime.now().toString()))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Laporan"

        reportType = intent.getIntExtra("reportType", 0)

        setDisplayedOptions(reportType)

        fetchAllCategory()

        detailReportViewModel.category.observe(this, {
            it?.let {
                val arrayCategory = arrayListOf<SpinnerCategory>()
                it.forEach { category ->
                    arrayCategory.add(SpinnerCategory(category.id, category.name))
                }

                categoryAdapter =
                    ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayCategory)
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerCategory.adapter = categoryAdapter
            }
        })

        detailReportViewModel.subCategory.observe(this, {
            it?.let {
                val arraySubCategory = arrayListOf<SpinnerCategory>()
                it.forEach { subCategory ->
                    arraySubCategory.add(SpinnerCategory(subCategory.id, subCategory.name))
                }

                subCategoryAdapter =
                    ArrayAdapter(this, android.R.layout.simple_spinner_item, arraySubCategory)
                subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerSubcategory.adapter = subCategoryAdapter
            }
        })

        binding.spinnerCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val selectedCategory = binding.spinnerCategory.selectedItem as SpinnerCategory
                    fetchSubCategory(selectedCategory.id)
                    selectedCategoryId = selectedCategory.id
                }
            }

        binding.spinnerSubcategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val selectetSubCategory =
                        binding.spinnerSubcategory.selectedItem as SpinnerCategory
                    selectedSubCategoryId = selectetSubCategory.id
                }
            }
        binding.etReportEnddate.setText(todayDate)
        binding.etReportStartdate.setText(todayDate)

        binding.etReportStartdate.setOnClickListener { chooseDate(binding.etReportStartdate) }
        binding.etReportEnddate.setOnClickListener { chooseDate(binding.etReportEnddate) }

        binding.btnSearchReport.setOnClickListener {
            when (reportType) {
                ReportType.ORDER.id -> getAllOrder()
                ReportType.UNFINISHED_ORDER.id -> getUnfinishedOrder()
                ReportType.FINISHED_ORDER.id -> getFinishedOrder()
                ReportType.PICKED_ORDER.id -> getPickedOrUnpickedOrder()
                else -> showMessage("Tipe laporan tidak tersedia")
            }
        }

        detailReportViewModel.error.observe(this, {
            it?.let {
                isLoading(false)
                showMessage(it)
            }
        })

        detailReportViewModel.order.observe(this, {
            it?.let {
                orders.clear()

                it.map { result ->
                    orders.add(result)
                }

                detailReportAdapter.setData(orders)

                with(binding.rvReport) {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    setHasFixedSize(true)
                    adapter = detailReportAdapter
                }

                detailReportAdapter.notifyDataSetChanged()

                binding.tvTotalReportTitle.visibility = View.VISIBLE
                binding.tvTotalReport.text = "${orders.size} order"

                isLoading(false)
            }
        })

        detailReportAdapter.onItemClick = {
            startActivity(Intent(this, DetailOrderReportActivity::class.java).putExtra("data", it))
        }
    }

    private fun setDisplayedOptions(reportType: Int) {
        when(reportType) {
            ReportType.ORDER.id -> {
                supportActionBar?.title = "Semua order"
                binding.cbDekorasi.visibility = View.GONE
                binding.radioGroupPickedorder.visibility = View.GONE
            }
            ReportType.PICKED_ORDER.id -> {
                supportActionBar?.title = "Pengambilan order"
                binding.tvCategoryTitle.visibility = View.GONE
                binding.spinnerCategory.visibility = View.GONE
                binding.tvSubcategoryTitle.visibility = View.GONE
                binding.spinnerSubcategory.visibility = View.GONE
                binding.cbDekorasi.visibility = View.GONE
            }
            ReportType.FINISHED_ORDER.id -> {
                supportActionBar?.title = "Order selesai"
                binding.tvCategoryTitle.visibility = View.GONE
                binding.spinnerCategory.visibility = View.GONE
                binding.tvSubcategoryTitle.visibility = View.GONE
                binding.spinnerSubcategory.visibility = View.GONE
                binding.cbDekorasi.visibility = View.GONE
                binding.radioGroupPickedorder.visibility = View.GONE
            }
            ReportType.UNFINISHED_ORDER.id -> {
                supportActionBar?.title = "Order belum selesai"
                binding.radioGroupPickedorder.visibility = View.GONE
            }
        }
    }

    private fun fetchAllCategory() {
        lifecycleScope.launch {
            detailReportViewModel.getCategory()
        }
    }

    private fun fetchSubCategory(id: Int) {
        lifecycleScope.launch {
            detailReportViewModel.getSubCategoryByCategoryId(id)
        }
    }

    private fun chooseDate(etDate: EditText) {
        val cal = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this@DetailReportActivity,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "d MMM yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                etDate.setText(sdf.format(cal.time))

                if (etDate == binding.etReportStartdate) {
                    binding.etReportEnddate.isEnabled = true
                }
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        if(etDate == binding.etReportEnddate) {
            val outputDateFormat = SimpleDateFormat("d MMM yyyy")
            val startDateMillis = outputDateFormat.parse(binding.etReportStartdate.text.toString())
            startDateMillis?.let {
                datePickerDialog.datePicker.minDate = it.time
            }
        }

        datePickerDialog.show()
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun isLoading(stat: Boolean) {
        if (stat) {
            binding.progressBarUpdate.visibility = View.VISIBLE
            binding.btnSearchReport.isEnabled = false
        } else {
            binding.progressBarUpdate.visibility = View.GONE
            binding.btnSearchReport.isEnabled = true
        }
    }

    private fun getAllOrder() {
        if (selectedCategoryId != 0
            && selectedSubCategoryId != 0
            && binding.etReportStartdate.text.toString().isNotBlank()
            && binding.etReportEnddate.text.toString().isNotBlank()
        ) {

            isLoading(true)
            val outputDateFormat = SimpleDateFormat("d MMM yyyy")
            val editDateFormat = SimpleDateFormat("yyyy-MM-dd")
            lifecycleScope.launch {
                detailReportViewModel.getReport(
                    reportType,
                    Report(
                        reportType,
                        editDateFormat.format(outputDateFormat.parse(binding.etReportStartdate.text.toString())),
                        editDateFormat.format(outputDateFormat.parse(binding.etReportEnddate.text.toString())),
                        selectedCategoryId,
                        selectedSubCategoryId,
                        null
                    )
                )
            }

        } else {
            showMessage("Data belum lengkap")
        }
    }

    private fun getUnfinishedOrder(){
        if (selectedCategoryId != 0
            && selectedSubCategoryId != 0
            && binding.etReportStartdate.text.toString().isNotBlank()
            && binding.etReportEnddate.text.toString().isNotBlank()
        ) {

            isLoading(true)
            val outputDateFormat = SimpleDateFormat("d MMM yyyy")
            val editDateFormat = SimpleDateFormat("yyyy-MM-dd")
            lifecycleScope.launch {
                detailReportViewModel.getReport(
                    reportType,
                    Report(
                        reportType,
                        editDateFormat.format(outputDateFormat.parse(binding.etReportStartdate.text.toString())),
                        editDateFormat.format(outputDateFormat.parse(binding.etReportEnddate.text.toString())),
                        selectedCategoryId,
                        selectedSubCategoryId,
                        binding.cbDekorasi.isChecked
                    )
                )
            }

        } else {
            showMessage("Data belum lengkap")
        }
    }

    private fun getFinishedOrder(){
        if (binding.etReportStartdate.text.toString().isNotBlank()
            && binding.etReportEnddate.text.toString().isNotBlank()
        ) {

            isLoading(true)
            val outputDateFormat = SimpleDateFormat("d MMM yyyy")
            val editDateFormat = SimpleDateFormat("yyyy-MM-dd")
            lifecycleScope.launch {
                detailReportViewModel.getReport(
                    reportType,
                    Report(
                        reportType,
                        editDateFormat.format(outputDateFormat.parse(binding.etReportStartdate.text.toString())),
                        editDateFormat.format(outputDateFormat.parse(binding.etReportEnddate.text.toString())),
                        null,
                        null,
                        null
                    )
                )
            }

        } else {
            showMessage("Data belum lengkap")
        }
    }

    private fun getPickedOrUnpickedOrder(){
        if (binding.etReportStartdate.text.toString().isNotBlank()
            && binding.etReportEnddate.text.toString().isNotBlank()
        ) {

            isLoading(true)
            val outputDateFormat = SimpleDateFormat("d MMM yyyy")
            val editDateFormat = SimpleDateFormat("yyyy-MM-dd")

            val checkedRadioButtonId = binding.radioGroupPickedorder.checkedRadioButtonId
            val type = if(checkedRadioButtonId == R.id.rb_picked){
                ReportType.PICKED_ORDER.id
            } else {
                ReportType.UNPICKED_ORDER.id
            }

            lifecycleScope.launch {
                detailReportViewModel.getReport(
                    type,
                    Report(
                        type,
                        editDateFormat.format(outputDateFormat.parse(binding.etReportStartdate.text.toString())),
                        editDateFormat.format(outputDateFormat.parse(binding.etReportEnddate.text.toString())),
                        null,
                        null,
                        null
                    )
                )
            }

        } else {
            showMessage("Data belum lengkap")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}