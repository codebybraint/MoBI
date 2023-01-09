package com.atiga.cakeorder.kitchen.ui.detail

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.atiga.cakeorder.core.network.response.KitchenOrderResponseItem
import com.atiga.cakeorder.kitchen.databinding.ActivityDetailBinding
import com.atiga.cakeorder.kitchen.ui.scanner.ScannerActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel: DetailViewModel by viewModel()
    private var detail: KitchenOrderResponseItem? = null
    private var isFinished: Boolean? = null
    private var image1: String? = null
    private var image2: String? = null
    private var image3: String? = null

    companion object {
        const val SCAN_QR_ACTIVITY = 1
        const val REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Detail pesanan"

        fetchData()

        binding.btnStart.setOnClickListener {
            isLoading(true)
            startJobDekor()
        }

        binding.btnFinish.setOnClickListener {
            checkPermission()
        }

        detailViewModel.jobDekorStarted.observe(this, {
            it?.let {
                if (it.isSuccess) {
                    val returnIntent = Intent()
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                    showMessage("Dekor dimulai")
                } else {
                    isLoading(false)
                    showMessage("Terjadi kesalahan")
                }
            }
        })

        detailViewModel.jobDekorFinished.observe(this, {
            it?.let {
                if (it.isSuccess) {
                    val returnIntent = Intent()
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                    showMessage("Dekor selesai")
                } else {
                    isLoading(false)
                    showMessage("Terjadi kesalahan")
                }
            }
        })

        detailViewModel.error.observe(this, {
            it?.let {
                isLoading(false)
                showMessage(it)
            }
        })

        binding.img1.setOnClickListener {
            startActivity(Intent(this, ImagePreviewActivity::class.java).putExtra("image", image1))
        }
        binding.img2.setOnClickListener {
            startActivity(Intent(this, ImagePreviewActivity::class.java).putExtra("image", image2))
        }
        binding.img3.setOnClickListener {
            startActivity(Intent(this, ImagePreviewActivity::class.java).putExtra("image", image3))
        }
    }

    private fun fetchData() {
        detail = intent.getParcelableExtra("detail")
        isFinished = intent.getBooleanExtra("isFinished", false)
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val outputDateFormat = SimpleDateFormat("d MMM yyyy, HH:mm")

        detail?.let { it ->
            binding.tvNoOrder.text = it.noPesanan
            binding.tvProductName.text = it.namaBarang
            it.dekorasi?.let { decoration ->
                binding.tvUcapan.text = decoration.ucapan
            } ?: run {
                binding.tvUcapan.visibility = View.GONE
                binding.tvUcapanTitle.visibility = View.GONE
            }
            binding.tvKeterangan.text = it.keterangan

            it.jobStarted?.let { stat ->
                if (stat) {
                    // decor started
                    binding.btnStart.visibility = View.GONE
                    binding.btnFinish.visibility = View.VISIBLE
                    it.detail.usrName?.let { binding.tvStaffName.text = it }
                    it.detail.tglMulai?.let {
                        binding.tvTglmulai.text = outputDateFormat.format(inputDateFormat.parse(it))
                    }
                    it.detail.tglSelesai?.let {
                        binding.tvTglselesai.text =
                            outputDateFormat.format(inputDateFormat.parse(it))
                    }
                } else {
                    binding.btnStart.visibility = View.VISIBLE
                    binding.btnFinish.visibility = View.GONE
                    binding.textView6.visibility = View.GONE
                    binding.textView9.visibility = View.GONE
                    binding.textView11.visibility = View.GONE
                    binding.tvStaffName.visibility = View.GONE
                    binding.tvTglmulai.visibility = View.GONE
                    binding.tvTglselesai.visibility = View.GONE
                    binding.view2.visibility = View.GONE
                }
            }

            it.tglAmbil?.let {
                binding.tvTglambil.text = outputDateFormat.format(inputDateFormat.parse(it))
            }

            it.images?.let { listImages ->
                if (listImages.isNotEmpty()) {
                    val imageByteArray = Base64.decode(listImages[0], Base64.DEFAULT)
                    image1 = listImages[0]
                    Glide.with(this)
                        .load(imageByteArray)
                        .centerCrop()
                        .into(binding.img1)
                    binding.img1.visibility = View.VISIBLE

                    if (listImages.size >= 2) {
                        val imageByteArray = Base64.decode(listImages[1], Base64.DEFAULT)
                        image2 = listImages[1]
                        Glide.with(this)
                            .load(imageByteArray)
                            .centerCrop()
                            .into(binding.img2)
                        binding.img2.visibility = View.VISIBLE
                    }

                    if (listImages.size == 3) {
                        val imageByteArray = Base64.decode(listImages[2], Base64.DEFAULT)
                        image3 = listImages[2]
                        Glide.with(this)
                            .load(imageByteArray)
                            .centerCrop()
                            .into(binding.img3)
                        binding.img3.visibility = View.VISIBLE
                    }

                    binding.scrollImgDecor.visibility = View.VISIBLE
                } else {
                    binding.scrollImgDecor.visibility = View.GONE
                }
            } ?: kotlin.run {
                binding.scrollImgDecor.visibility = View.GONE
            }
        }

        isFinished?.let {
            if (it) {
                binding.btnStart.visibility = View.GONE
                binding.btnFinish.visibility = View.GONE
            }
        }
    }

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

    private fun startJobDekor() {
        lifecycleScope.launch {
            detail?.let {
                if (it.noPesanan != null && it.itemKey != null) {
                    detailViewModel.startDekor(it.noPesanan!!, it.itemKey!!)
                }
            }
        }
    }

    private fun finishJobDekor(userKey: String) {
        lifecycleScope.launch {
            detail?.let {
                it.urutKey?.let { urutKey ->
                    if (it.noPesanan != null && it.itemKey != null) {
                        detailViewModel.finishDekor(userKey, it.noPesanan!!, it.itemKey!!, urutKey)
                    }
                }
            }
        }
    }

    private fun isLoading(stat: Boolean) {
        if (stat) {
            binding.btnStart.isEnabled = false
            binding.btnFinish.isEnabled = false
            binding.progressBarUpdate.visibility = View.VISIBLE
        } else {
            binding.btnStart.isEnabled = true
            binding.btnFinish.isEnabled = true
            binding.progressBarUpdate.visibility = View.GONE
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SCAN_QR_ACTIVITY && resultCode == Activity.RESULT_OK) {
            val userKey = data?.getStringExtra("result")
            isLoading(true)
            userKey?.let { finishJobDekor(it) }
        }
    }
}