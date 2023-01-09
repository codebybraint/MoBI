package com.atiga.cakeorder.ui.order.input

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.util.LruCache
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.order.*
import com.atiga.cakeorder.core.domain.model.product.Product
import com.atiga.cakeorder.databinding.ActivityInputOrderBinding
import com.atiga.cakeorder.databinding.BottomSheetAddProductBinding
import com.atiga.cakeorder.databinding.BottomSheetOrderMenuBinding
import com.atiga.cakeorder.ui.order.input.orderitem.AddItemActivity
import com.atiga.cakeorder.ui.order.input.orderitem.AddedItemAdapter
import com.atiga.cakeorder.ui.order.input.orderitem.ListOrderItemActivity
import com.atiga.cakeorder.ui.scanner.ScannerActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


@ExperimentalCoroutinesApi
@FlowPreview
class InputOrderActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityInputOrderBinding
    private val inputOrderViewModel: InputOrderViewModel by viewModel()
    private var addedProducts = mutableListOf<Product>()
    private var listItems = mutableListOf<OrderAddItem>()
    private val addedItemAdapter = AddedItemAdapter()
    private lateinit var submitMenuItem: MenuItem
    private var isDecorationProductExists = false
    private lateinit var memoryCache: LruCache<String, Bitmap>

    companion object {
        const val SCAN_QR_ACTIVITY = 1
        const val REQUEST_CODE = 2
        const val ADD_ITEM = 3
        const val ORDER_ITEM_DETAIL = 4
        const val ADD_IMG_DECOR_1 = 11
        const val ADD_IMG_DECOR_2 = 12
        const val ADD_IMG_DECOR_3 = 13
        const val REQ_MULTIPLE_PERMISSION = 111
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.input_order)
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8

        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {

            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.byteCount / 1024
            }
        }

        //add nama pemesan to query (search like)
        binding.atNamaPemesan.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                lifecycleScope.launch {
                    inputOrderViewModel.nameQueryChannel.send(s.toString())
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        //add telp pemesan to query (search like)
        binding.atTelponPemesan.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                lifecycleScope.launch {
                    inputOrderViewModel.phoneQueryChannel.send(s.toString())
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        //search nama like pemesan, lalu tampilkan ke et
        inputOrderViewModel.nameSearchResult.observe(this, { result ->
            val names = arrayListOf<String?>()
            result.map {
                names.add(it.name)
            }
            val adapter =
                ArrayAdapter(
                    this@InputOrderActivity,
                    android.R.layout.select_dialog_item,
                    names.distinct()
                )
            adapter.notifyDataSetChanged()
            binding.atNamaPemesan.setAdapter(adapter)
        })
        //search nomor like pemesan, lalu tampilkan ke et
        inputOrderViewModel.phoneSearchResult.observe(this, { result ->
            val phones = arrayListOf<String?>()
            result.map {
                phones.add(it.phoneNumber)
            }
            val adapter =
                ArrayAdapter(
                    this@InputOrderActivity,
                    android.R.layout.select_dialog_item,
                    phones.distinct()
                )
            adapter.notifyDataSetChanged()
            binding.atTelponPemesan.setAdapter(adapter)
        })

        // onClickListener btnAddItem
        //TODO (1. click btn Add item)
        binding.btnAddItem.setOnClickListener {
            startActivityForResult(Intent(this, AddItemActivity::class.java), ADD_ITEM)
        }
        // onClickListener cbLunas
        binding.cbLunas.setOnCheckedChangeListener { _, b ->
            binding.etJumlahDp.isEnabled = !b
            if (b) {
                binding.etJumlahDp.text.clear()
            }
        }
        //handler add order
        inputOrderViewModel.addedOrder.observe(this, {
            it?.let {
                Toast.makeText(this, "Order berhasil dibuat", Toast.LENGTH_LONG).show()
                val returnIntent = Intent()
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        })
        // handler error
        inputOrderViewModel.error.observe(this, {
            it?.let {
                showMessage(it)
                isLoading(false)
            }
        })

        setDefaultDayAndHour()
        // onClickListener tgl Ambil
        binding.etTanggalAmbil.setOnClickListener {
            chooseDate(binding)
        }
        // onClickListener jam Ambil
        binding.etJamAmbil.setOnClickListener {
            chooseTime(binding)
        }

    }

    fun getBitmapFromMemoryCache(key: String?):Bitmap?{
        return memoryCache?.get(key)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startActivityForResult(Intent(this, ScannerActivity::class.java), SCAN_QR_ACTIVITY)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_submit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        } else if (item.itemId == R.id.action_submit) {
            if (validateInputData()) {
                submitMenuItem = item
                checkPermission()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (binding.atNamaPemesan.text.isNotBlank()
            || binding.atTelponPemesan.text.isNotBlank()
            || listItems.isNotEmpty()
        ) {
            showAlertDialog()
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("cekk", "onActivityResult: reqcode $requestCode")
        Log.d("cekk", "onActivityResult: rescode $resultCode")
        if (requestCode == SCAN_QR_ACTIVITY && resultCode == Activity.RESULT_OK) {
            val userKey = data?.getStringExtra("result")
            isLoading(true)
            userKey?.let { submitOrder(it) }
        } else if (requestCode == ADD_ITEM && resultCode == Activity.RESULT_OK) {
            with(binding.rvProductItem) {
                binding.parentOrder.requestFocus()
                val product = data?.getParcelableExtra<Product>("result")
                product?.let { result ->
                    val existingItemCount = listItems.filter { item -> item.idProduct == result.id }

                    if (existingItemCount.isNotEmpty()) {
                        listItems.find { item -> item.idProduct == result.id }
                            ?.let { existingItem ->
                                existingItem.totalQuantity += result.addAmount
                                if (!result.isDecorate) {
                                    existingItem.listOrderItem[0].quantity =
                                        existingItem.totalQuantity
                                }
                            }
                    } else {
                        if (result.isDecorate) {
                            listItems.add(
                                OrderAddItem(
                                    idProduct = result.id,
                                    productName = result.name,
                                    totalQuantity = result.addAmount,
                                    arrayListOf(
                                        OrderAddItemDetail(
                                            result.addAmount,
                                            "",
                                            Decoration("")
                                        )
                                    )
                                )
                            )
                        } else {
                            listItems.add(
                                OrderAddItem(
                                    idProduct = result.id,
                                    productName = result.name,
                                    totalQuantity = result.addAmount,
                                    arrayListOf(OrderAddItemDetail(result.addAmount, ""))
                                )
                            )
                        }
                    }

                }

                fetchData()

                // copy description
                addedItemAdapter.onCopyItemClick = {
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("description", it)
                    clipboard.setPrimaryClip(clip)
                    hideKeyboard()
                    Toast.makeText(this@InputOrderActivity, "Ucapan disalin", Toast.LENGTH_SHORT)
                        .show();
                }
            }
        } else if (requestCode == ORDER_ITEM_DETAIL && resultCode == Activity.RESULT_OK) {
            val result = data?.getParcelableArrayListExtra<OrderAddItemDetail>("result")
            val itemPosition = data?.getIntExtra("id", 0)
            result?.let {
                itemPosition?.let { position ->
                    listItems[position].listOrderItem.clear()
                    listItems[position].listOrderItem.addAll(it)

                    fetchData()
                }
            }
        }
    }

    private fun fetchData() {
        with(binding.rvProductItem) {
            addedItemAdapter.setData(listItems)
            layoutManager = LinearLayoutManager(
                this@InputOrderActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            setHasFixedSize(true)
            adapter = addedItemAdapter
            addedItemAdapter.notifyItemInserted(addedProducts.size - 1)

            addedItemAdapter.onItemClick = { item, productName, totalQuantity, position ->
                item?.let { showBottomSheetDialogMenu(it, productName, totalQuantity, position) }
            }

            isDecorationProductExists = false
            listItems.forEach {
                it.listOrderItem.forEach { item ->
                    item.decoration?.let { isDecorationProductExists = true }
                }
            }
        }
        addedItemAdapter.notifyDataSetChanged()
    }

    // camera permission
    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.CAMERA
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    REQUEST_CODE
                )

                // REQUEST_CODE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            startActivityForResult(Intent(this, ScannerActivity::class.java), SCAN_QR_ACTIVITY)
        }
    }

    // hide keyboard
    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        parent?.getItemAtPosition(pos)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun chooseDate(binding: ActivityInputOrderBinding) {
        val cal = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this@InputOrderActivity,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "d MMM yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.etTanggalAmbil.setText(sdf.format(cal.time))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    //show time picker dialog
    private fun chooseTime(binding: ActivityInputOrderBinding) {
        val cal = Calendar.getInstance()
        val hh = cal.get(Calendar.HOUR_OF_DAY)
        val mm = cal.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this@InputOrderActivity,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                binding.etJamAmbil.setText(String.format("%02d:%02d", hourOfDay, minute))
            },
            hh,
            mm,
            true
        )

        timePickerDialog.show()
    }

    //set edit text tgl ambil dan jam ambil
    private fun setDefaultDayAndHour(){
        val cal = Calendar.getInstance()
        val myFormat = "d MMM yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.etTanggalAmbil.setText(sdf.format(cal.time))

        val hh = cal.get(Calendar.HOUR_OF_DAY)
        val mm = cal.get(Calendar.MINUTE)
        binding.etJamAmbil.setText(String.format("%02d:%02d", hh, mm))
    }

    private fun showBottomSheetDialogMenu(
        orderItem: List<OrderAddItemDetail>,
        productName: String,
        totalQuantity: Int,
        position: Int
    ) {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_order_menu, null)
        val binding = BottomSheetOrderMenuBinding.inflate(
            layoutInflater,
            bottomSheetView as ViewGroup,
            false
        )
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(binding.root)

        binding.btnEditOrderitemQty.setOnClickListener {
            dialog.dismiss()
            showBottomSheetDialogQuantity(
                totalQuantity,
                productName,
                listItems[position].listOrderItem[0].description,
                position
            )
        }

        binding.btnEditOrderitemDetail.setOnClickListener {
            dialog.dismiss()
            Log.e("Activity","This")
            //TODO bikin tiap kali add product ke menu edit dl
            val intent = Intent(this@InputOrderActivity, ListOrderItemActivity::class.java)
                .putExtra("id", position)
                .putExtra("productName", listItems[position].productName)
                .putExtra("decoration", listItems[position].listOrderItem[0].decoration)
                .putExtra("totalQuantity", listItems[position].totalQuantity)
            orderItem.let {
                val orderItemDetaildata = arrayListOf<OrderAddItemDetail>()
                orderItemDetaildata.addAll(it)
                intent.putParcelableArrayListExtra("data", orderItemDetaildata)
            }
            startActivityForResult(intent, ORDER_ITEM_DETAIL)
        }

        dialog.show()
    }

    private fun showBottomSheetDialogQuantity(
        quantity: Int,
        productName: String,
        description: String,
        position: Int
    ) {
        Log.d("cek", "showBottomSheetDialogMenu: ${description}")
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_add_product, null)
        val binding = BottomSheetAddProductBinding.inflate(
            layoutInflater,
            bottomSheetView as ViewGroup,
            false
        )
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(binding.root)

        Glide.with(this).load(R.drawable.default_cake).into(binding.imageView9)
        binding.tvName.text = productName
        binding.tvAmount.text = quantity.toString()
        binding.tvDesc.text = description
        binding.btnAdd.text = "Simpan"

        binding.btnAmountMin.setOnClickListener {
            if(binding.tvAmount.text.toString().toInt() > 1){
                binding.tvAmount.text = (binding.tvAmount.text.toString().toInt() - 1).toString()
                binding.btnAmountMin.isEnabled = true
                binding.btnAdd.visibility = View.VISIBLE
                binding.btnDeleteOrderitem.visibility = View.GONE
            } else {
                binding.tvAmount.text = "0"
                binding.btnAmountMin.isEnabled = false
                binding.btnAdd.visibility = View.GONE
                binding.btnDeleteOrderitem.visibility = View.VISIBLE
            }
        }

        binding.btnAmountPlus.setOnClickListener {
            binding.tvAmount.text = (binding.tvAmount.text.toString().toInt() + 1).toString()
            binding.btnAdd.visibility = View.VISIBLE
            binding.btnAmountMin.isEnabled = true
            binding.btnDeleteOrderitem.visibility = View.GONE
        }

        binding.btnAdd.setOnClickListener {
            listItems[position].totalQuantity = binding.tvAmount.text.toString().toInt()
            if(listItems[position].listOrderItem[0].decoration == null){
                listItems[position].listOrderItem[0].quantity = binding.tvAmount.text.toString().toInt()
            }
            fetchData()
            dialog.dismiss()
        }

        binding.btnDeleteOrderitem.setOnClickListener {
            listItems.removeAt(position)
            fetchData()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.dialog_title_warning)
        builder.setMessage(R.string.dialog_backpressed_order_message)
        builder.setPositiveButton(R.string.dialog_backpressed_yes) { dialog, _ ->
            finish()
        }

        builder.setNegativeButton(R.string.dialog_backpressed_no) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    /*
    minta list uri sebagai data,
     */

    private fun submitOrder(userKey: String) {
        val orderItemList = mutableListOf<ItemOrder>()
        var downPayment: Double? = null
        if(!binding.cbLunas.isChecked){
            val cleanDownPaymentInput = binding.etJumlahDp.text.toString().replace(
                ",".toRegex(),
                ""
            ).trim().toDouble()
            downPayment = cleanDownPaymentInput
        }

        listItems.forEach { item ->
            item.listOrderItem.forEach { itemDetail ->
                itemDetail.images?.let {
                    orderItemList.add(
                        ItemOrder(
                            item.idProduct,
                            itemDetail.description,
                            itemDetail.decoration,
                            itemDetail.quantity,
                            flag = false,
                            //TODO here
//                            images = getBitmapFromMemoryCache("bmp1")
                            images = itemDetail.images

                        )
                    )
                } ?: kotlin.run {
                    orderItemList.add(
                        ItemOrder(
                            item.idProduct,
                            itemDetail.description,
                            itemDetail.decoration,
                            itemDetail.quantity,
                            flag = false
                        )
                    )
                }

            }
        }

        lifecycleScope.launch {
            inputOrderViewModel.addOrder(
                userKey,
                AddOrder(
                    binding.atTelponPemesan.text.toString(),
                    binding.cbLunas.isChecked,
                    downPayment,
                    orderItemList,
                    binding.atNamaPemesan.text.toString(),
                    convertDateAndTime(
                        binding.etTanggalAmbil.text.toString(),
                        binding.etJamAmbil.text.toString()
                    )
                )
            )
        }
    }

    private fun validateInputData(): Boolean {
        val inputDateFormat = SimpleDateFormat("d MMM yyyy", Locale.US)
        val currentDate = Calendar.getInstance().time
        var check = true

        if (binding.atNamaPemesan.text.isNullOrBlank()
            || binding.atTelponPemesan.text.isNullOrBlank()
            || listItems.isNullOrEmpty()
            || (!binding.cbLunas.isChecked && binding.etJumlahDp.text.isNullOrBlank())
            || binding.etTanggalAmbil.text.isNullOrBlank()
            || binding.etJamAmbil.text.isNullOrBlank()
        ) {
            check = false
            showMessage("Data belum lengkap")
        } else {
            run breaking@ {
                listItems.forEach {
                    var totalQtyPerItem = 0
                    it.listOrderItem.forEach { itemDetail ->
                        totalQtyPerItem += itemDetail.quantity

//                     check description data
//                        if (itemDetail.description.isNullOrBlank()
//                        ) {
//                            check = false
//                            showMessage("Data item pesanan belum lengkap")
//                            return@breaking
//                        }
//                         check pickup date
//                        if(itemDetail.pickupDate.equals(inputDateFormat.format(currentDate).toString())){
//                            check = true
//                            showMessage("Cek kapasitas hari ini")
//                            return@breaking
//                        }

                    }

                    // check whether total order item detail quantity is equal to item total quantity
                    if (totalQtyPerItem != it.totalQuantity) {
                        check = false
                        showMessage("Jumlah item tidak sesuai")
                        return@breaking
                    }

                }
            }

        }
        return check
    }

    private fun convertDateAndTime(date: String, time: String): String {
        val inputDateFormat = SimpleDateFormat("d MMM yyyy")
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd")

        return "${outputDateFormat.format(inputDateFormat.parse(date))}T$time:00.000+07:00"
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun isLoading(stat: Boolean) {
        if (stat) {
            binding.progressBarUpdate.visibility = View.VISIBLE
            submitMenuItem.isEnabled = false
        } else {
            binding.progressBarUpdate.visibility = View.GONE
            submitMenuItem.isEnabled = true
        }
    }


}