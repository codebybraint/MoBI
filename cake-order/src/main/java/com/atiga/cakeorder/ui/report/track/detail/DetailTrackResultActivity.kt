package com.atiga.cakeorder.ui.report.track.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.atiga.cakeorder.core.domain.model.order.Order
import com.atiga.cakeorder.databinding.ActivityDetailOrderBinding
import com.atiga.cakeorder.ui.report.track.TrackOrderAdapter
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat

class DetailTrackResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailOrderBinding
    private var order: Order? = null
    private val trackOrderAdapter = TrackOrderAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Detail pesanan"

        order = intent.getParcelableExtra("data")

        order?.let {
            val formatter: NumberFormat = DecimalFormat("#,###")
            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val outputDateFormat = SimpleDateFormat("d MMM yyyy, HH:mm")

            binding.tvDetailOrdernumber.text = it.orderNumber
            binding.tvDetailCustomerName.text = it.customerName
            binding.tvDetailPhone.text = it.phoneNumber
            it.isPaid?.let { paid ->
                if(paid){
                    binding.tvDetailPaymentStatus.text = "Lunas"
                } else {
                    it.downPayment?.let { dp ->
                        binding.tvDetailPaymentStatus.text = "DP - Rp ${formatter.format(dp)}"
                    }
                }
            }
            it.orderDate?.let { binding.tvDetailOrderdate.text = outputDateFormat.format(inputDateFormat.parse(it))}
            it.tanggalAmbil?.let { binding.tvDetailTanggalAmbil.text = outputDateFormat.format(inputDateFormat.parse(it))}

            trackOrderAdapter.setData(it.items)
            with(binding.rvDetailOrderitem){
                layoutManager = LinearLayoutManager(this@DetailTrackResultActivity, LinearLayoutManager.VERTICAL, false)
                adapter = trackOrderAdapter
            }
        }

        trackOrderAdapter.onItemClick = { orderNumber, itemKey, productName, hasBeenCanceled ->
            val intent = Intent(this, DetailTrackOrderActivity::class.java)
                .putExtra("orderNumber", orderNumber)
                .putExtra("itemKey", itemKey)
                .putExtra("productName", productName)
                .putExtra("hasBeenCanceled", hasBeenCanceled)
            startActivity(intent)
        }

        binding.btnCopyOrdernumber.setOnClickListener {
            val clipboard = this.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("orderNumber", binding.tvDetailOrdernumber.text.toString())
            clipboard.setPrimaryClip(clip)
            showMessage("Nomor pesanan disalin")
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