package com.atiga.cakeorder.ui.user.detail

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.user.User
import com.atiga.cakeorder.databinding.ActivityDetailUserBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import java.io.ByteArrayOutputStream

class DetailUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailUserBinding
    private var user: User? = null
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Detail user"

        user = intent.getParcelableExtra("data")

        user?.let {
            bitmap = generateQRCode(it.userId)
            binding.imgQrcode.setImageBitmap(bitmap)
            binding.tvUsername.text = it.username
            binding.tvUserRole.text = it.userRole.roleName
        }

    }

    private fun generateQRCode(text: String): Bitmap {
        val width = 300
        val height = 300
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()
        try {
            val bitMatrix = codeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        } catch (e: WriterException) {
            Log.d("qr code failed", "generateQRCode: ${e.message}")
        }
        return bitmap
    }

    private fun shareQRcode() {
        try {
            val bitmap = getBitmapFromView(binding.constraintQrcode)
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(this, bitmap))
            shareIntent.type = "image/jpeg"
            startActivity(Intent.createChooser(shareIntent, "Share"))
        } catch (e: Exception) {
            e.message
        }
    }

    //create bitmap from view and returns it
    private fun getBitmapFromView(view: ConstraintLayout): Bitmap? {
        try {
            val returnedBitmap =
                Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(returnedBitmap)
            val bgDrawable = view.background
            if (bgDrawable != null) {
                //has background drawable, then draw it on the canvas
                bgDrawable.draw(canvas)
            } else {
                //does not have background drawable, then draw white background on the canvas
                canvas.drawColor(Color.WHITE)
            }
            view.draw(canvas)
            return returnedBitmap
        } catch (e: java.lang.Exception) {
            Log.d("share failed", "getBitmapFromView: $e")
        }
        return null
    }


    private fun getImageUri(inContext: Context, inImage: Bitmap?): Uri? {
        try {
            val bytes = ByteArrayOutputStream()
            inImage!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(
                inContext.getContentResolver(),
                inImage, "QR - ${user?.username}", "image"
            )
            return Uri.parse(path)
        } catch (e: Exception) {
            e.message
        }
        return null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_detail_user, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        } else if (item.itemId == R.id.action_share) {
            shareQRcode()
        }
        return super.onOptionsItemSelected(item)
    }
}