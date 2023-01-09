package com.atiga.cakeorder.kitchen.ui.main

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.atiga.cakeorder.core.domain.model.user.User
import com.atiga.cakeorder.core.network.response.KitchenOrderResponseItem
import com.atiga.cakeorder.core.utils.Status
import com.atiga.cakeorder.kitchen.R
import com.atiga.cakeorder.kitchen.databinding.ActivityMainBinding
import com.atiga.cakeorder.kitchen.databinding.BottomSheetFilterOrderitemBinding
import com.atiga.cakeorder.kitchen.ui.detail.DetailActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModel()
    private var listItemOrder = mutableListOf<KitchenOrderResponseItem>()
    private val mainAdapter = MainAdapter()
    private lateinit var dialog: BottomSheetDialog
    private var users = mutableListOf<User>()
    private var isStarted = false
    private var date: String? = null
    private var jobStartDate: String? = null
    private var jobEndDate: String? = null
    private var selectedStaffIndex = -1
    private var selectedStaffId: String? = null
    private var tmpIsStarted = false
    private var tmpDate: String? = null
    private var tmpJobStartDate: String? = null
    private var tmpJobEndDate: String? = null
    private var tmpSelectedStaffIndex = -1
    private var tmpSelectedStaffId: String? = null

    companion object {
        const val DETAIL_ORDER = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            mainViewModel.getOrderToWork(fIsStarted = false)
            mainViewModel.login(
                getString(R.string.kitchen_user),
                getString(R.string.kitchen_pass)
            )
        }

        fetchData()

        mainViewModel.login.observe(this, {
            it?.let {
                lifecycleScope.launch {
                    mainViewModel.getAllUser(it.token)
                }
            }
        })

        mainViewModel.userData.observe(this, {
            it?.let {
                users.clear()
                users.addAll(it)
            }
        })

        mainAdapter.onItemClick = { data, isFinished ->
            startActivityForResult(
                Intent(this@MainActivity, DetailActivity::class.java)
                    .putExtra("detail", data)
                    .putExtra("isFinished", isFinished),
                DETAIL_ORDER
            )
        }

        binding.chipFilter.setOnClickListener {
            showBottomSheetDialog()
        }
    }

    private fun fetchData() {
        mainViewModel.orderData.observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        listItemOrder.clear()

                        it.data?.map { result ->
                            if (!result.hasBeenCanceled!!) {
                                listItemOrder.add(result)
                            }
                        }

                        mainAdapter.setData(listItemOrder)

                        with(binding.rvOrder) {
                            layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            setHasFixedSize(true)
                            adapter = mainAdapter
                        }

                        mainAdapter.notifyDataSetChanged()
                        isLoading(false)

                        if (listItemOrder.size < 1) {
                            binding.imgOrderEmpty.visibility = View.VISIBLE
                            binding.tvOrderEmpty.visibility = View.VISIBLE
                            binding.rvOrder.visibility = View.GONE
                        } else {
                            binding.imgOrderEmpty.visibility = View.GONE
                            binding.tvOrderEmpty.visibility = View.GONE
                            binding.rvOrder.visibility = View.VISIBLE
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

    private fun showBottomSheetDialog() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_filter_orderitem, null)
        val filterBinding = BottomSheetFilterOrderitemBinding.inflate(
            layoutInflater,
            bottomSheetView as ViewGroup,
            false
        )
        dialog = BottomSheetDialog(this)
        dialog.setContentView(filterBinding.root)

        // show existing filter data
        if (isStarted) {
            jobStartDate?.let {
                filterBinding.etDate1.setText(convertDateReverse(it))
            }
            jobEndDate?.let {
                filterBinding.etDate2.setText(convertDateReverse(it))
            }
            filterBinding.rbSudah.isChecked = true
            filterBinding.tvTanggalTitle.text = "Tanggal pengerjaan"
            filterBinding.etDate1.hint = "Mulai dekor"
            filterBinding.etDate2.hint = "Selesai dekor"
            filterBinding.etDate2.visibility = View.VISIBLE
            filterBinding.tvStaffTitle.visibility = View.VISIBLE
            filterBinding.chipgroupStaff.visibility = View.VISIBLE
        } else {
            date?.let {
                filterBinding.etDate1.setText(convertDateReverse(it))
            }
            filterBinding.rbBelum.isChecked = true
        }

        filterBinding.chipgroupStaff.removeAllViews()
        users.forEachIndexed { index, user ->
            val chip = Chip(this)
            chip.text = user.username
            chip.id = index
            chip.isCheckable = true
            chip.isClickable = true

            if (index == selectedStaffIndex) {
                chip.isChecked = true
            }

            filterBinding.chipgroupStaff.addView(chip)
        }

        if (isStarted || date != null) {
            filterBinding.btnRemoveFilter.visibility = View.VISIBLE
        } else {
            filterBinding.btnRemoveFilter.visibility = View.GONE
        }

        filterBinding.chipgroupStaff.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId < 1) {
                tmpSelectedStaffIndex = -1
            } else {
                tmpSelectedStaffIndex = checkedId
            }
        }

        filterBinding.btnFilterOrderitem.setOnClickListener {
            isStarted = tmpIsStarted
            if (tmpIsStarted) {
                // sudah dikerjakan
                date = null
                jobStartDate = if (!filterBinding.etDate1.text.toString().isNullOrBlank()) {
                    convertDate(filterBinding.etDate1.text.toString())
                } else {
                    null
                }
                jobEndDate = if (!filterBinding.etDate2.text.toString().isNullOrBlank()) {
                    convertDate(filterBinding.etDate2.text.toString())
                } else {
                    null
                }
                selectedStaffId = if (tmpSelectedStaffIndex > 0) {
                    users.get(tmpSelectedStaffIndex).userId
                } else {
                    null
                }
                selectedStaffIndex = tmpSelectedStaffIndex
            } else {
                // belum atau sedang dikerjakan
                date = if (!filterBinding.etDate1.text.toString().isNullOrBlank()) {
                    convertDate(filterBinding.etDate1.text.toString())
                } else {
                    null
                }
                jobStartDate = null
                jobEndDate = null
            }

            lifecycleScope.launch {
                mainViewModel.getOrderToWork(
                    fIsStarted = isStarted,
                    fDate = date,
                    fJobStartDate = jobStartDate,
                    fJobEndDate = jobEndDate,
                    fUser = selectedStaffId
                )
            }
            fetchData()

            dialog.dismiss()
        }

        filterBinding.btnRemoveFilter.setOnClickListener {
            isStarted = false
            date = null
            jobStartDate = null
            jobEndDate = null
            selectedStaffIndex = -1
            lifecycleScope.launch {
                mainViewModel.getOrderToWork(
                    fIsStarted = false,
                )
            }
            fetchData()

            dialog.dismiss()
        }

        filterBinding.radioGroupDecor.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_belum) {
                // belum dikerjakan
                tmpIsStarted = false
                filterBinding.tvTanggalTitle.text = "Tanggal pengambilan"
                filterBinding.etDate1.setText("")
                filterBinding.etDate2.visibility = View.INVISIBLE
                filterBinding.etDate1.hint = ""
                filterBinding.tvStaffTitle.visibility = View.GONE
                filterBinding.chipgroupStaff.visibility = View.GONE
            } else {
                // sudah dikerjakan
                tmpIsStarted = true
                filterBinding.tvTanggalTitle.text = "Tanggal pengerjaan"
                filterBinding.etDate1.hint = "Mulai"
                filterBinding.etDate2.hint = "Selesai"
                filterBinding.etDate1.setText("")
                filterBinding.etDate2.setText("")
                filterBinding.etDate2.visibility = View.VISIBLE
                filterBinding.tvStaffTitle.visibility = View.VISIBLE
                filterBinding.chipgroupStaff.visibility = View.VISIBLE
            }
        }

        filterBinding.etDate1.setOnClickListener {
            chooseDate(filterBinding, filterBinding.etDate1)
        }

        filterBinding.etDate2.setOnClickListener {
            chooseDate(filterBinding, filterBinding.etDate2)
        }

        dialog.show()
    }

    private fun chooseDate(binding: BottomSheetFilterOrderitemBinding, editText: EditText) {
        val cal = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this@MainActivity,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "d MMM yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                editText.setText(sdf.format(cal.time))

                if (editText == binding.etDate1) {
                    binding.etDate2.isEnabled = true
                }
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
//        if(editText == binding.etDate2){
//            val outputDateFormat = SimpleDateFormat("d MMM yyyy")
//            val startDateMillis = outputDateFormat.parse(binding.etDate1.text.toString())
//            startDateMillis?.let {
//                datePickerDialog.datePicker.minDate = it.time
//            }
//        }
        datePickerDialog.show()
    }

    private fun convertDate(date: String): String {
        val inputDateFormat = SimpleDateFormat("d MMM yyyy")
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd")

        return outputDateFormat.format(inputDateFormat.parse(date))
    }

    private fun convertDateReverse(date: String): String {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputDateFormat = SimpleDateFormat("d MMM yyyy")

        return outputDateFormat.format(inputDateFormat.parse(date))
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(stat: Boolean) {
        if (stat) {
            binding.progressBar.visibility = View.VISIBLE
            binding.rvOrder.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.rvOrder.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("cek", "onResume: main")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DETAIL_ORDER && resultCode == Activity.RESULT_OK) {
            isStarted = false
            lifecycleScope.launch {
                mainViewModel.getOrderToWork(fIsStarted = false)
                mainViewModel.login(
                    getString(R.string.kitchen_user),
                    getString(R.string.kitchen_pass)
                )
            }
            fetchData()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }else if (item.itemId == R.id.action_refresh) {
            recreate()
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_refresh, menu)
        return true
    }
}