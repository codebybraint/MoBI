package com.atiga.cakeorder.ui.order.detail

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.order.ItemOrder
import com.atiga.cakeorder.core.domain.model.order.Order
import com.atiga.cakeorder.databinding.ActivityDetailOrderBinding
import com.atiga.cakeorder.ui.order.edit.EditOrderActivity
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat

class DetailOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailOrderBinding
    private var detailOrderAdapter = DetailOrderAdapter()
    private val detailOrderViewModel: DetailOrderViewModel by viewModel()
    private var order: Order? = null
    private var orderNumber: String? = null

    companion object {
        const val EDIT_ORDER = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Detail pesanan"

        orderNumber = intent.getStringExtra("orderNumber")
        fetchData()

        detailOrderAdapter.onItemClick = { isFinishJobDekor, jobStarted, isFinishedOrder ->
            if(jobStarted && !isFinishJobDekor){
                showMessage("Order masih dalam proses dekorasi")
            }
            else if(!jobStarted) {
                showMessage("Order belum didekorasi")
            }
            else if(isFinishedOrder){
                showMessage("Order sudah selesai")
            }
        }

        detailOrderAdapter.onButtonPickItem = { orderNumber, itemKey, _ ->
            lifecycleScope.launch {
                detailOrderViewModel.finishOrder(
                    orderNumber,
                    itemKey
                )
            }
        }

        detailOrderViewModel.finishOrder.observe(this, {
            it?.let {
                fetchData()
                showMessage("Order selesai")
            }
        })

        binding.btnCopyOrdernumber.setOnClickListener {
            val clipboard = this.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("orderNumber", binding.tvDetailOrdernumber.text.toString())
            clipboard.setPrimaryClip(clip)
            showMessage("Nomor pesanan disalin")
        }

        detailOrderViewModel.error.observe(this, {
            it?.let {
                showMessage(it)
            }
        })
    }

    private fun fetchData(){
        val formatter: NumberFormat = DecimalFormat("#,###")
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val outputDateFormat = SimpleDateFormat("d MMM yyyy, HH:mm")

        orderNumber?.let {
            lifecycleScope.launch {
                detailOrderViewModel.getOrderDetail(it)
            }
        }

        detailOrderViewModel.detailOrder.observe(this, {
            it?.let {
                order = it
                Log.d("ORDER",it.items.toString())
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
                detailOrderAdapter.setPickupDateNonDekor(it.tanggalAmbil)
                detailOrderAdapter.setData(it.items.filter { data -> data.hasBeenCanceled == false })
                with(binding.rvDetailOrderitem){
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    setHasFixedSize(true)
                    adapter = detailOrderAdapter
                }
                detailOrderAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_order_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        } else if(item.itemId == R.id.action_edit_order){

            order?.let {
                val arrayOrderItem = arrayListOf<ItemOrder>()
                arrayOrderItem.addAll(it.items)
                startActivityForResult(Intent(this@DetailOrderActivity, EditOrderActivity::class.java).putExtra("orderNumber", orderNumber), EDIT_ORDER)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == EDIT_ORDER && resultCode == Activity.RESULT_OK) {
            fetchData()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val returnIntent = Intent()
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}