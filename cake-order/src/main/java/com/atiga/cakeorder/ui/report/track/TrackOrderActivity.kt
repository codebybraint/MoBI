package com.atiga.cakeorder.ui.report.track

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.atiga.cakeorder.core.domain.model.order.Order
import com.atiga.cakeorder.databinding.ActivityTrackOrderBinding
import com.atiga.cakeorder.ui.report.track.detail.DetailTrackResultActivity
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class TrackOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrackOrderBinding
    private val trackOrderViewModel: TrackOrderViewModel by viewModel()
    private val trackResultAdapter = TrackResultAdapter()
    var startDateFilter: String = ""
    var endDateFilter: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Lacak order"

        val outputDateFormat = SimpleDateFormat("d MMM yyyy")
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val todayDate = outputDateFormat.format(inputDateFormat.parse(LocalDateTime.now().toString()))

        binding.etTrackStartdate.setText(todayDate)
        binding.etTrackEnddate.setText(todayDate)

        binding.btnSearchOrderWithNamePhone.setOnClickListener {
            var name = ""
            var phone = ""

            val outputDateFormat = SimpleDateFormat("d MMM yyyy")
            val editDateFormat = SimpleDateFormat("yyyy-MM-dd")

            startDateFilter = editDateFormat.format(outputDateFormat.parse(binding.etTrackStartdate.text.toString()))
            endDateFilter = editDateFormat.format(outputDateFormat.parse(binding.etTrackEnddate.text.toString()))


            if(binding.etOrderName.text.toString().isBlank()
                && binding.etOrderPhone.text.toString().isBlank()){
                showMessage("Nama/telepon pemesan harus diisi")
            } else {
                if(binding.etOrderName.text.toString().isNotBlank()){
                    name = binding.etOrderName.text.toString().trim()
                    if(binding.etOrderPhone.text.toString().isNotBlank()){
                        phone = binding.etOrderPhone.text.toString()
                    }
                } else {
                    if(binding.etOrderPhone.text.toString().isNotBlank()){
                        phone = binding.etOrderPhone.text.toString()
                    }
                }
                lifecycleScope.launch {
                    isLoading(true)
                    hideKeyboard()
                    trackOrderViewModel.getOrderByNameAndOrPhone(startDateFilter, endDateFilter, name, phone)
                }
            }
        }

        trackOrderViewModel.orderResult.observe(this, {
            isLoading(false)
            if(it.isNotEmpty()){

                trackResultAdapter.setData(it)

                with(binding.rvTrackResult){
                    layoutManager = LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
                    setHasFixedSize(true)
                    adapter = trackResultAdapter
                }

                binding.rvTrackResult.visibility = View.VISIBLE
            } else {
                showMessage("Pesanan tidak ditemukan")
                binding.rvTrackResult.visibility = View.GONE
            }
        })

        trackOrderViewModel.error.observe(this, {
            it?.let {
                isLoading(false)
                showMessage("Pesanan tidak ditemukan")
                binding.rvTrackResult.visibility = View.GONE
            }
        })

        trackResultAdapter.onItemClick = { order ->
            val intent = Intent(this, DetailTrackResultActivity::class.java)
                .putExtra("data",order)
                startActivity(intent)
        }

        binding.etTrackStartdate.setOnClickListener {
            chooseDate(binding.etTrackStartdate)
        }
        binding.etTrackEnddate.setOnClickListener {
            chooseDate(binding.etTrackEnddate)
        }
    }

    private fun chooseDate(etDate: EditText) {
        val cal = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this@TrackOrderActivity,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "d MMM yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                etDate.setText(sdf.format(cal.time))

                if (etDate == binding.etTrackStartdate) {
                    binding.etTrackEnddate.isEnabled = true
                }
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        if(etDate == binding.etTrackEnddate) {
            val outputDateFormat = SimpleDateFormat("d MMM yyyy")
            val startDateMillis = outputDateFormat.parse(binding.etTrackStartdate.text.toString())
            startDateMillis?.let {
                datePickerDialog.datePicker.minDate = it.time
            }
        }

        datePickerDialog.show()
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun isLoading(stat: Boolean){
        if(stat){
            binding.progressBarUpdate.visibility = View.VISIBLE
//            binding.btnSearchOrdernumber.isEnabled = false
            binding.btnSearchOrderWithNamePhone.isEnabled = false
        } else {
            binding.progressBarUpdate.visibility = View.GONE
//            binding.btnSearchOrdernumber.isEnabled = true
            binding.btnSearchOrderWithNamePhone.isEnabled = true
        }
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}