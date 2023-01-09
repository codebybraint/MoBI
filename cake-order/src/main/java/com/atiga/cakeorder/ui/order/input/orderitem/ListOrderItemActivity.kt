package com.atiga.cakeorder.ui.order.input.orderitem

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.order.Decoration
import com.atiga.cakeorder.core.domain.model.order.OrderAddItemDetail
import com.atiga.cakeorder.databinding.ActivityListOrderItemBinding

class ListOrderItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListOrderItemBinding
    private var itemPosition = 0
    private var listOrderItemDetail = ArrayList<OrderAddItemDetail>()
    private val listOrderItemAdapter = ListOrderItemAdapter()
    private var totalQuantity = 0
    private var remainingQty = 0
    private var decoration: Decoration? = null

    companion object{
        const val ADD_ORDER_ITEM_DETAIL = 1
        const val EDIT_ORDER_ITEM_DETAIL = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListOrderItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val productName = intent.getStringExtra("productName")
        itemPosition = intent.getIntExtra("id", 0)
        val data = intent.getParcelableArrayListExtra<OrderAddItemDetail>("data")
        decoration = intent.getParcelableExtra("decoration")
        totalQuantity = intent.getIntExtra("totalQuantity",0)

        supportActionBar?.title = productName

        data?.let {
            listOrderItemDetail.addAll(it)
            fetchData()
        }

        if(decoration != null){
            binding.btnAddOrderitemDetail.setOnClickListener {
                startActivityForResult(Intent(this@ListOrderItemActivity, EditOrderItemActivity::class.java)
                    .putExtra("remainingQuantity", remainingQty), ADD_ORDER_ITEM_DETAIL
                )
            }
        } else {
            binding.btnAddOrderitemDetail.visibility = View.INVISIBLE
        }

    }

    private fun fetchData() {
        listOrderItemAdapter.setData(listOrderItemDetail)

        with(binding.rvOrderitemDetail) {
            layoutManager = LinearLayoutManager(
                this@ListOrderItemActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            setHasFixedSize(true)
            adapter = listOrderItemAdapter
            listOrderItemAdapter.notifyItemInserted(listOrderItemDetail.size-1)
        }

        // count remaining item to be added
        var qty = 0
        listOrderItemDetail.forEach {
            qty += it.quantity
        }
        remainingQty = totalQuantity - qty

        // hide add more item detail option if remaining qty is 0
        if(remainingQty==0 || decoration==null){
            binding.btnAddOrderitemDetail.visibility = View.INVISIBLE
        } else {
            binding.btnAddOrderitemDetail.visibility = View.VISIBLE
        }

        listOrderItemAdapter.onItemClick = { item, position ->
            var otherItemTotalQty = 0
            listOrderItemDetail.forEachIndexed { index, orderAddItemDetail ->
                if(index != position){
                    otherItemTotalQty += orderAddItemDetail.quantity
                }
            }

            val intent = Intent(this@ListOrderItemActivity, EditOrderItemActivity::class.java)
                .putExtra("data", item)
                .putExtra("id", position)
                .putExtra("addedOrderItemDetailCount", listOrderItemDetail.size)
                .putExtra("remainingQuantity", totalQuantity - otherItemTotalQty)
            startActivityForResult(intent, EDIT_ORDER_ITEM_DETAIL)

            Log.d("cekk", "fetchData: listorderitem ${item.images?.size} images ")
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
            val returnIntent = Intent()
                .putParcelableArrayListExtra("result", listOrderItemDetail)
                .putExtra("id", itemPosition)

            // grant temporary permission to URI
            listOrderItemDetail.forEach {
                it.imagesUri?.forEach { image ->
                    returnIntent.data = image
                }
                returnIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_ORDER_ITEM_DETAIL && resultCode == Activity.RESULT_OK){
            val result = data?.getParcelableExtra<OrderAddItemDetail>("result")
            result?.let {
                listOrderItemDetail.add(it)
                fetchData()
            }
        } else if(requestCode == EDIT_ORDER_ITEM_DETAIL && resultCode == Activity.RESULT_OK){
            val result = data?.getParcelableExtra<OrderAddItemDetail>("result")
            val id = data?.getIntExtra("id",0)
            val removedId = data?.getIntExtra("removedId", 0)
            result?.let {
                id?.let { position ->
                    listOrderItemDetail[position].quantity = it.quantity
                    listOrderItemDetail[position].description = it.description
                    listOrderItemDetail[position].decoration?.let { decoration -> decoration.ucapan = it.decoration?.ucapan }
                    it.images?.let { listImages ->
                        listOrderItemDetail[position].images = listImages
                        Log.d("cekk", "onActivityResult: listorder images ${listImages.size}")
                    } ?: kotlin.run {
                        listOrderItemDetail[position].images = null
                    }
                    it.imagesUri?.let { listImagesUri ->
                        listOrderItemDetail[position].imagesUri = listImagesUri
                        Log.d("cekk", "onActivityResult: listorder imagesUri ${listImagesUri.size}")
                    } ?: kotlin.run {
                        listOrderItemDetail[position].imagesUri = null
                    }
                }
                fetchData()
            }
            if(result == null){
                removedId?.let {
                    listOrderItemDetail.removeAt(it)
                    fetchData()
                }
            }
        }
    }
}