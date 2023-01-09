package com.atiga.cakeorder.ui.order.edit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.order.CancelOrder
import com.atiga.cakeorder.core.domain.model.order.EditOrderItem
import com.atiga.cakeorder.core.domain.model.order.ItemOrder
import com.atiga.cakeorder.databinding.ActivityEditOrderBinding
import com.atiga.cakeorder.databinding.BottomSheetCancelOrderBinding
import com.atiga.cakeorder.databinding.BottomSheetEditOrderitemBinding
import com.atiga.cakeorder.util.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class EditOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditOrderBinding
    private val editOrderViewModel: EditOrderViewModel by viewModel()
    private var itemOrder = mutableListOf<ItemOrder>()
    private var editOrderAdapter = EditOrderAdapter()
    private var orderNumber: String? = null
    private lateinit var dialog: BottomSheetDialog
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Ubah pesanan"

        sessionManager = SessionManager(this)

        fetchData()

        editOrderAdapter.onItemClick = { item, position, isNotEditable ->
            if (isNotEditable) {
                showMessage("Order tidak dapat diubah")
            } else {
                item.dekorasi?.let {
                    showBottomSheetDialog(item, position)
                }
            }
        }

        editOrderViewModel.error.observe(this, {
            it?.let {
                dialog.dismiss()
                showMessage(it)
            }
        })

        editOrderViewModel.orderEdited.observe(this, {
            it?.let {
                fetchData()
                dialog.dismiss()
                showMessage("Data diubah")
            }
        })

        editOrderViewModel.cancelOrder.observe(this,{
            it?.let {
                fetchData()
                dialog.dismiss()
                showMessage("Pesanan dibatalkan")
            }
        })
    }

    private fun fetchData() {
        orderNumber = intent.getStringExtra("orderNumber")

        orderNumber?.let {
            lifecycleScope.launch {
                editOrderViewModel.getOrderDetail(it)
            }
        }

        editOrderViewModel.detailOrder.observe(this, {
            it?.let {
                itemOrder.addAll(it.items.filter { data -> data.hasBeenCanceled == false })

                editOrderAdapter.setData(it.items.filter { data -> data.hasBeenCanceled == false })
                with(binding.rvOrderitemEdit) {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    setHasFixedSize(true)
                    adapter = editOrderAdapter
                }
                editOrderAdapter.notifyDataSetChanged()
            }
        })

    }

    private fun editOrder(
        orderNumber: String,
        itemKey: Int,
        data: EditOrderItem,
        position: Int,
        dialog: BottomSheetDialog
    ) {
        lifecycleScope.launch {
            editOrderViewModel.editOrder(orderNumber, itemKey, data)
        }
    }

    private fun showCancelDialog(orderNumber: String, itemKey: Int) {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_cancel_order, null)
        val binding = BottomSheetCancelOrderBinding.inflate(
            layoutInflater,
            bottomSheetView as ViewGroup,
            false
        )
        dialog = BottomSheetDialog(this)
        dialog.setContentView(binding.root)
        bottomSheetView.setOnClickListener { dialog.dismiss() }

        binding.btnCancelOrder.setOnClickListener {
            if (!binding.etCancelName.text.isNullOrBlank() && !binding.etCancelReason.text.isNullOrBlank()) {
                binding.btnCancelOrder.isEnabled = false
                lifecycleScope.launch {
                    editOrderViewModel.cancelOrder(
                        CancelOrder(
                            orderNumber,
                            itemKey,
                            binding.etCancelName.text.toString(),
                            binding.etCancelReason.text.toString()
                        ),
                        sessionManager.getFromPreference(SessionManager.USER_ID)!!
                    )
                }
            } else {
                showMessage("Data belum lengkap")
            }
        }

        dialog.show()
    }

    private fun showBottomSheetDialog(data: ItemOrder, position: Int) {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_edit_orderitem, null)
        val binding = BottomSheetEditOrderitemBinding.inflate(
            layoutInflater,
            bottomSheetView as ViewGroup,
            false
        )
        dialog = BottomSheetDialog(this)
        dialog.setContentView(binding.root)
        bottomSheetView.setOnClickListener { dialog.dismiss() }

        binding.tvOrderitemName.text = data.namaBarang

        data.dekorasi?.let {
            binding.etOrderitemUcapan.setText(it.ucapan)
        } ?: run {
            binding.tvOrderitemUcapanTitle.visibility = View.GONE
            binding.etOrderitemUcapan.visibility = View.GONE
        }

        binding.btnEditOrderitem.setOnClickListener {
            if (data.dekorasi != null) {
                if (binding.etOrderitemUcapan.text.isNotBlank()) {
                    itemOrder?.get(position)?.dekorasi?.ucapan =
                        binding.etOrderitemUcapan.text.toString()
                    itemOrder?.get(position)?.let { item ->
                        editOrder(
                            item.noPesanan!!,
                            item.itemKey!!,
                            EditOrderItem(
                                item.itemKey!!,
                                binding.etOrderitemUcapan.text.toString()
                            ),
                            position,
                            dialog
                        )
                    }
                } else {
                    showMessage("Data belum lengkap")
                }
            }
        }

        binding.btnCancelOrder.setOnClickListener {
            dialog.dismiss()
            itemOrder?.get(position)?.let { item ->
                showCancelDialog(item.noPesanan!!, item.itemKey!!)
            }
        }

        dialog.show()
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.dialog_title_info)
        builder.setMessage(R.string.dialog_edit_order_info_message)
        builder.setPositiveButton(R.string.dialog_backpressed_ok) { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_edit_order, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        } else if (item.itemId == R.id.action_edit_info) {
            showAlertDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val returnIntent = Intent()
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}