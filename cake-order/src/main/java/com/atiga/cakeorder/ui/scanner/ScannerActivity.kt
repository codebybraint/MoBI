package com.atiga.cakeorder.ui.scanner

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.atiga.cakeorder.R
import com.atiga.cakeorder.databinding.ActivityScannerBinding
import com.google.zxing.Result
import me.dm7.barcodescanner.core.IViewFinder
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    private lateinit var binding: ActivityScannerBinding
    private lateinit var mScannerView: ZXingScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.scan_qr)

        initScannerView()
    }

    private fun initScannerView() {
        mScannerView = object : ZXingScannerView(this) {
            override fun createViewFinderView(context: Context?): IViewFinder {
                return CustomViewFinderView(context!!)
            }
        }
        mScannerView.setAutoFocus(true)
        mScannerView.setResultHandler(this)
        binding.frameScanner.addView(mScannerView)
    }

    override fun onStart() {
        mScannerView.startCamera()
//        doRequestPermission()
        super.onStart()
    }

    private fun doRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100 -> {
                initScannerView()
            }
            else -> {
                /* nothing to do in here */
            }
        }
    }

    override fun onPause() {
        mScannerView.stopCamera()
        super.onPause()
    }

    override fun handleResult(rawResult: Result?) {
        Log.d("scanqr", "handleResult: ${rawResult?.text}")
        val returnIntent = Intent().putExtra("result", rawResult?.text)
        setResult(Activity.RESULT_OK, returnIntent)

        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}