package com.atiga.cakeorder.ui.report.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.atiga.cakeorder.core.domain.model.order.Order
import com.atiga.cakeorder.databinding.ActivityDetailOrderReportBinding
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat

class DetailOrderReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailOrderReportBinding
    private val detailOrderAdapter = DetailOrderReportAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailOrderReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Detail pesanan"

        fetchData()

        binding.btnCopyOrdernumber.setOnClickListener {
            val clipboard = this.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("orderNumber", binding.tvDetailOrdernumber.text.toString())
            clipboard.setPrimaryClip(clip)
            showMessage("Nomor pesanan disalin")
        }
    }

    private fun fetchData() {
        val order = intent.getParcelableExtra<Order>("data")
        val formatter: NumberFormat = DecimalFormat("#,###")
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val outputDateFormat = SimpleDateFormat("d MMM yyyy")

        order?.let {
            binding.tvDetailOrdernumber.text = it.orderNumber
            binding.tvDetailCustomerName.text = it.customerName
            binding.tvDetailPhone.text = it.phoneNumber
            it.isPaid?.let { paid ->
                if (paid) {
                    binding.tvDetailPaymentStatus.text = "Lunas"
                } else {
                    it.downPayment?.let { dp ->
                        binding.tvDetailPaymentStatus.text = "DP - Rp ${formatter.format(dp)}"
                    }
                }
            }
            it.orderDate?.let {
                binding.tvDetailOrderdate.text = outputDateFormat.format(inputDateFormat.parse(it))
            }
            it.tanggalAmbil?.let {
                binding.tvDetailPickupdate.text = outputDateFormat.format(inputDateFormat.parse(it))
            } ?: kotlin.run {
                binding.tvDetailPickupdate.visibility = View.GONE
                binding.textView11.visibility = View.GONE
            }

            detailOrderAdapter.setData(it.items)
            with(binding.rvDetailOrderitem) {
                layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                setHasFixedSize(true)
                adapter = detailOrderAdapter
            }
        }

    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}