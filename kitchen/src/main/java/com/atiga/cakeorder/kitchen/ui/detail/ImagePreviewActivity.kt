package com.atiga.cakeorder.kitchen.ui.detail

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import com.atiga.cakeorder.kitchen.databinding.ImagePreviewBinding
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.davemorrissey.labs.subscaleview.ImageSource
import com.atiga.cakeorder.kitchen.R

class ImagePreviewActivity: AppCompatActivity() {
    private lateinit var binding: ImagePreviewBinding
    private lateinit var submitMenuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ImagePreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        val data = intent.getStringExtra("image")
        data?.let {
            val imageByteArray = Base64.decode(it, Base64.DEFAULT)
            val imageBitmap = BitmapFactory.decodeByteArray(
                imageByteArray,
                0,
                imageByteArray.size
            )
            val imgRotation: Bitmap? = null
            binding.imageView.setImage(ImageSource.bitmap(imageBitmap))
            binding.imageView.isZoomEnabled = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }else if (item.itemId == R.id.action_rotate) {
            binding.imageView.orientation = (binding.imageView.orientation + 90) % 360
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        binding.imageView.orientation
        super.onBackPressed()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_rotate, menu)
        return true
    }

}