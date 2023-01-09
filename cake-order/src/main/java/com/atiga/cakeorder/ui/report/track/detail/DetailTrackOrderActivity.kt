package com.atiga.cakeorder.ui.report.track.detail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.atiga.cakeorder.core.domain.model.track.Tracking
import com.atiga.cakeorder.databinding.ActivityDetailTrackOrderBinding
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class DetailTrackOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailTrackOrderBinding
    private val detailTrackOrderViewModel: DetailTrackOrderViewModel by viewModel()
    private val detailTrackOrderAdapter = DetailTrackOrderAdapter()
    private var orderNumber: String? = ""
    private var itemKey = 0
    private var productName: String? = ""
    private var hasBeenCanceled: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTrackOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Detail pelacakan"

        fetchData()

        detailTrackOrderViewModel.trackedOrder.observe(this, {
            it?.let {

                if (hasBeenCanceled!!) {
                    val listDetail = mutableListOf<Tracking>()
                    listDetail.add(
                        Tracking(
                            it[0].trackingDate,
                            it[0].trackingStatus,
                            3
                        )
                    )
                    listDetail.add(
                        Tracking(
                            null,
                            "Pesanan dibatalkan",
                            2
                        )
                    )
                    detailTrackOrderAdapter.setData(listDetail)
                } else {
                    detailTrackOrderAdapter.setData(it)
                }

                with(binding.rvTracking) {
                    layoutManager = LinearLayoutManager(
                        this@DetailTrackOrderActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    adapter = detailTrackOrderAdapter
                }

                isLoading(false)
            }
        })

        detailTrackOrderViewModel.error.observe(this, {
            it?.let {
                isLoading(false)
                showMessage(it)
            }
        })
    }

    private fun fetchData() {
        orderNumber = intent.getStringExtra("orderNumber")
        itemKey = intent.getIntExtra("itemKey", 0)
        productName = intent.getStringExtra("productName")
        hasBeenCanceled = intent.getBooleanExtra("hasBeenCanceled", false)

        if (!orderNumber.isNullOrBlank() && itemKey != 0) {
            isLoading(true)
            lifecycleScope.launch {
                orderNumber?.let { detailTrackOrderViewModel.trackOrder(it, itemKey) }
            }
        }

        orderNumber?.let { binding.tvOrderNumber.text = it }
        productName?.let { binding.tvProductName.text = it }
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun isLoading(stat: Boolean) {
        if (stat) {
            binding.progressBarUpdate.visibility = View.VISIBLE
        } else {
            binding.progressBarUpdate.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}