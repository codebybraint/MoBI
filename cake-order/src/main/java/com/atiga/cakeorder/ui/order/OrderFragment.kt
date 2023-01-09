package com.atiga.cakeorder.ui.order

import android.app.Activity
import android.app.SearchManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Filterable
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.order.Order
import com.atiga.cakeorder.core.utils.Status
import com.atiga.cakeorder.databinding.OrderFragmentBinding
import com.atiga.cakeorder.ui.order.detail.DetailOrderActivity
import com.atiga.cakeorder.ui.order.detail.DetailOrderViewModel
import com.atiga.cakeorder.ui.order.input.InputOrderActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

@FlowPreview
@ExperimentalCoroutinesApi
class OrderFragment : Fragment() {

    private val orderViewModel: OrderViewModel by viewModel()
    private var _binding: OrderFragmentBinding? = null
    private val binding get() = _binding!!
    private val orderAdapter = OrderAdapter()
    private var orders = mutableListOf<Order>()
    private lateinit var searchView: SearchView
    private lateinit var searchItem: MenuItem

    companion object {
        const val ADD_ORDER = 1
        const val DETAIL_ORDER = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = OrderFragmentBinding.inflate(inflater, container,false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.fabInputOrder.setOnClickListener { startActivityForResult(Intent(activity, InputOrderActivity::class.java), ADD_ORDER) }

        fetchData()

        orderAdapter.onItemClick = {
            searchView.onActionViewCollapsed()
            searchItem.collapseActionView()
            val intent = Intent(context, DetailOrderActivity::class.java).putExtra("orderNumber", it)
            startActivityForResult(intent, DETAIL_ORDER)
        }

        orderAdapter.onLongItemClick = {
            val clipboard = activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("orderNumber", it)
            clipboard.setPrimaryClip(clip)
            showMessage("Nomor pesanan disalin")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){
        //TODO
        inflater.inflate(R.menu.menu_add_item, menu)

        searchItem = menu.findItem(R.id.action_search)
        searchView = MenuItemCompat.getActionView(searchItem) as SearchView
//        searchView.setOnCloseListener { true }

        val searchPlate =
            searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
        searchPlate.hint = "Cari Order"
        val searchPlateView: View = searchView.findViewById(androidx.appcompat.R.id.search_plate)
        searchPlateView.setBackgroundColor( android.R.color.transparent
//            ContextCompat.getColor(
//                parentFragment.context,
//                android.R.color.transparent
//            )
        )

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                    //TODO bikin handler disini
                orderAdapter.filter.filter(newText)
                return false
            }
        })

        val searchManager =
            parentFragment?.activity!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(parentFragment?.activity!!.componentName))

        if (searchView.query.toString() != null && searchView.query.isEmpty()) searchItem.collapseActionView()
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun fetchData(){
        lifecycleScope.launch {
            orderViewModel.getUnfinishedOrder()
        }

        orderViewModel.orderData.observe(viewLifecycleOwner, {
            it?.let { resource ->
                when(resource.status){
                    Status.SUCCESS -> {
                        orders.clear()
                        it.data?.map { result ->
                            var showOrder = false
                            for (data in result.items){
                                if(!data.hasBeenCanceled!! && data.pickupDate==null){
                                    showOrder = true
//                                    Log.d("IMAGES",data.images.toString())
                                    break
                                }
                            }
                            /*
                            * show order if there's at least 1 item with:
                            * hasBeenCanceled = false
                            * pickupDate = null (belum diambil)
                            * */
                            if(showOrder){
                                orders.add(result)
                            }
                        }

                        orderAdapter.setData(orders)
                        with(binding.rvOrder){
                            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            setHasFixedSize(true)
                            adapter = orderAdapter
                        }

                        orderAdapter.notifyItemInserted(orders.size-1)
                        orderAdapter.showData()
                        isLoading(false)

                        if(orders.size < 1){
                            binding.imgOrderEmpty.visibility = View.VISIBLE
                            binding.tvOrderEmpty.visibility = View.VISIBLE
                        } else {
                            binding.imgOrderEmpty.visibility = View.GONE
                            binding.tvOrderEmpty.visibility = View.GONE
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

    private fun showMessage(msg: String){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(bool: Boolean){
        if(bool){
            binding.rvOrder.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.rvOrder.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_ORDER && resultCode == Activity.RESULT_OK){
            fetchData()
        } else if(requestCode == DETAIL_ORDER && resultCode == Activity.RESULT_OK){
            fetchData()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }else if (item.itemId == R.id.search_close_btn) {
            searchView.isIconified = true
        }
        return super.onOptionsItemSelected(item)
    }

    fun onBackPressed() {
        if (!searchView.isIconified) {
            searchView.onActionViewCollapsed()
            searchItem.collapseActionView()
        } else {
        }
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }
}