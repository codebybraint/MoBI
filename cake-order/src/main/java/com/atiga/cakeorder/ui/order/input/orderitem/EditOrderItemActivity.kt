package com.atiga.cakeorder.ui.order.input.orderitem

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.util.LruCache
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.order.Decoration
import com.atiga.cakeorder.core.domain.model.order.OrderAddItemDetail
import com.atiga.cakeorder.databinding.ActivityEditOrderItemBinding
import com.atiga.cakeorder.ui.order.input.InputOrderActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class EditOrderItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditOrderItemBinding
    private var id: Int? = null
    private var orderItemData: OrderAddItemDetail? = null
    private var remainingQuantity = 0
    private var imageUri1: Uri? = null
    private var imageUri2: Uri? = null
    private var imageUri3: Uri? = null
    private var tempImageUri1: Uri? = null
    private var tempImageUri2: Uri? = null
    private var tempImageUri3: Uri? = null
    private var imageBitmap1: Bitmap? = null
    private var imageBitmap2: Bitmap? = null
    private var imageBitmap3: Bitmap? = null
    private var imagePath1: String? = null
    private var imagePath2: String? = null
    private var imagePath3: String? = null
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f
    private lateinit var memoryCache: LruCache<String, Bitmap>

    companion object {
        const val ADD_IMG_DECOR_1 = 11
        const val ADD_IMG_DECOR_2 = 12
        const val ADD_IMG_DECOR_3 = 13
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //caching
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8


        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }

        binding = ActivityEditOrderItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail order item"

        fetchData()
        checkAndRequestPermissions()

        binding.btnDeleteOrderitemDetail.setOnClickListener {
            id?.let { showAlertDialog(it) }
        }

        // image decor 1
        binding.imgDecor1.setOnClickListener {
            showImageDialog(ADD_IMG_DECOR_1)
        }

        binding.btnAddDecor1.setOnClickListener {
            openImageIntent(ADD_IMG_DECOR_1)
        }

        binding.btnChangeImg1.setOnClickListener {
            openImageIntent(ADD_IMG_DECOR_1)
        }

        binding.btnRemoveImg1.setOnClickListener {
            imageUri1 = null
            binding.btnAddDecor1.visibility = View.VISIBLE
            binding.imgDecor1.visibility = View.INVISIBLE
            binding.btnRemoveImg1.visibility = View.GONE
            binding.btnChangeImg1.visibility = View.GONE
        }

        binding.imgDecor2.setOnClickListener {
            showImageDialog(ADD_IMG_DECOR_2)
        }

        binding.btnAddDecor2.setOnClickListener {
            openImageIntent(ADD_IMG_DECOR_2)
        }

        binding.btnChangeImg2.setOnClickListener {
            openImageIntent(ADD_IMG_DECOR_2)
        }

        binding.btnRemoveImg2.setOnClickListener {
            imageUri2 = null
            binding.btnAddDecor2.visibility = View.VISIBLE
            binding.imgDecor2.visibility = View.INVISIBLE
            binding.btnRemoveImg2.visibility = View.GONE
            binding.btnChangeImg2.visibility = View.GONE
        }

        binding.imgDecor3.setOnClickListener {
            showImageDialog(ADD_IMG_DECOR_3)
        }

        binding.btnAddDecor3.setOnClickListener {
            openImageIntent(ADD_IMG_DECOR_3)
        }

        binding.btnChangeImg3.setOnClickListener {
            openImageIntent(ADD_IMG_DECOR_3)
        }

        binding.btnRemoveImg3.setOnClickListener {
            imageUri3 = null
            binding.btnAddDecor3.visibility = View.VISIBLE
            binding.imgDecor3.visibility = View.INVISIBLE
            binding.btnRemoveImg3.visibility = View.GONE
            binding.btnChangeImg3.visibility = View.GONE
        }
    }
    fun addBitmapToMemoryCache(key: String, bitmap: Bitmap?){
        if(getBitmapFromMemoryCache(key) == null) memoryCache?.put(key, bitmap)
    }

    fun getBitmapFromMemoryCache(key: String?):Bitmap?{
        return memoryCache?.get(key)
    }

    /*
    fungsi ini untuk mengambil data yang dikirimkan dari intent
    dengan menggunakan extra. Extra dengan key:
        1. data =  digunakan untuk passing data berupa List,
        yang berisi class OrderAddItemDetail
        2. remainingQuantitiy = digunakan nantinya untuk hint,
        berapakah quantity terisisa dari item tersebut.
     */
    private fun fetchData() {
        id = intent.getIntExtra("id", 0)
        orderItemData = intent.getParcelableExtra("data")
        remainingQuantity = intent.getIntExtra("remainingQuantity", 0)

        orderItemData?.let {
            binding.etOrderitemQty.setText(Integer.toString(it.quantity))
            binding.etOrderitemDesc.setText(it.description)
            it.decoration?.let { decoration ->
                binding.atOrderitemUcapan.setText(decoration.ucapan)
                binding.scrollImgDecor.visibility = View.VISIBLE
            } ?: run {
                binding.etOrderitemQty.isEnabled = false
                binding.tvTitleUcapan.visibility = View.GONE
                binding.atOrderitemUcapan.visibility = View.GONE
                binding.scrollImgDecor.visibility = View.GONE
            }
            binding.btnDeleteOrderitemDetail.visibility = View.VISIBLE
            // Harus diganti untuk handler images
            loadExistingImages(it.images, it.imagesUri)
        }

        binding.etOrderitemQty.hint = "Max: $remainingQuantity"
        val ucapan = resources.getStringArray(R.array.list_ucapan)
        val ucapanAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ucapan)
        binding.atOrderitemUcapan.setAdapter(ucapanAdapter)
    }

    private fun loadExistingImages(images: List<String>?, imagesUri: List<Uri>?){
        images?.let {
            if(it.isNotEmpty()){
                imagesUri?.get(0).let { image ->
                    Glide.with(this)
                        .load(image)
                        .centerCrop()
                        .into(binding.imgDecor1)
                    imageUri1 = image

                    binding.btnAddDecor1.visibility = View.INVISIBLE
                    binding.imgDecor1.visibility = View.VISIBLE
                    binding.btnChangeImg1.visibility = View.VISIBLE
                    binding.btnRemoveImg1.visibility = View.VISIBLE
                    Log.d("cekk", "loadExistingImages: 1")
                }
            }
            if(it.size >= 2){
                imagesUri?.let { image ->
                    if(image.size >= 2){
                        Glide.with(this)
                            .load(image[1])
                            .centerCrop()
                            .into(binding.imgDecor2)
                        imageUri2 = image[1]
                    }

                    binding.btnAddDecor2.visibility = View.INVISIBLE
                    binding.imgDecor2.visibility = View.VISIBLE
                    binding.btnChangeImg2.visibility = View.VISIBLE
                    binding.btnRemoveImg2.visibility = View.VISIBLE
                    Log.d("cekk", "loadExistingImages: 2")
                }
            }
            if(it.size == 3 ){
                imagesUri?.let { image ->
                    if(image.size == 3){
                        Glide.with(this)
                            .load(image[2])
                            .centerCrop()
                            .into(binding.imgDecor3)
                        imageUri3 = image[2]
                    }
                    binding.btnAddDecor3.visibility = View.INVISIBLE
                    binding.imgDecor3.visibility = View.VISIBLE
                    binding.btnChangeImg3.visibility = View.VISIBLE
                    binding.btnRemoveImg3.visibility = View.VISIBLE
                    Log.d("cekk", "loadExistingImages: 3")
                }
            }
        }
    }

    private fun checkImageRotation(path: String, bitmap: Bitmap): Bitmap{
        val ei = ExifInterface(path)
        val orientation: Int = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        Log.d("cekk", "checkImageRotation: $orientation")

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> bitmap
        }
    }

    private fun openImageIntent(resultCode: Int) {
        // Determine Uri of camera image to save.
        val root = File(
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + "CakeOrder" + File.separator
        )
        root.mkdirs()
        val fname = "${System.currentTimeMillis()}.jpg"
        val sdImageMainDirectory = File(root, fname)
        val uri = Uri.fromFile(sdImageMainDirectory)
        when (resultCode) {
            ADD_IMG_DECOR_1 -> tempImageUri1 = uri
            ADD_IMG_DECOR_2 -> tempImageUri2 = uri
            ADD_IMG_DECOR_3 -> tempImageUri3 = uri
        }
        // Camera.
        val cameraIntents: MutableList<Intent> = ArrayList()
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val packageManager = packageManager
        val listCam = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val packageName = res.activityInfo.packageName
            val intent = Intent(captureIntent)
            intent.component = ComponentName(packageName, res.activityInfo.name)
            intent.setPackage(packageName)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            cameraIntents.add(intent)
        }
        // Filesystem.
        val galleryIntent = Intent()
        galleryIntent.type = "image/*"
        galleryIntent.action = Intent.ACTION_PICK

        // Chooser of filesystem options.
        val chooserIntent = Intent.createChooser(galleryIntent, "Select Source")

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toTypedArray())
        startActivityForResult(chooserIntent, resultCode)
    }

    private fun showImageDialog(resultCode: Int) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_image_preview)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val imgView = dialog.findViewById<ImageView>(R.id.img_preview)
        var imageUri: Uri? = null

        when (resultCode) {
            ADD_IMG_DECOR_1 -> imageUri = imageUri1
            ADD_IMG_DECOR_2 -> imageUri = imageUri2
            ADD_IMG_DECOR_3 -> imageUri = imageUri3
        }

        imageUri?.let {
            imgView.setImageURI(imageUri)
        }

        dialog.findViewById<ImageButton>(R.id.btn_close_preview).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    //TODO ubah fungsi ini jadi fungsi simpan ke cache
    private fun getEncoded64ImageStringFromBitmap(): List<String> {
        val listImages = arrayListOf<String>()
        imageUri1?.let {
            try {
                val bitmap = decodeUri(this, imageUri1!!)

//                addBitmapToMemoryCache("bmp1",bitmap)
                var rotatedBitmap: Bitmap? = null

                //check image rotation if image taken from camera
                imagePath1?.let {
                    bitmap?.let { it1 ->
                        rotatedBitmap = checkImageRotation(it, it1)
                    }
                }
                val stream = ByteArrayOutputStream()
                rotatedBitmap?.compress(Bitmap.CompressFormat.JPEG, 80, stream) ?: kotlin.run {
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                }
                val byteFormat: ByteArray = stream.toByteArray()

                // Get the Base64 string
                listImages.add(Base64.encodeToString(byteFormat, Base64.DEFAULT))
                var img1Encoded64 = Base64.encodeToString(byteFormat, Base64.DEFAULT)
                Log.d("cekk", "Encoded : {${img1Encoded64.toString()}}")
            } catch (e: Exception){
                Log.e("cekk exception", "getEncoded64ImageStringFromBitmap: ${e.message}")
            }
        }

        imageUri2?.let {
            try {
                val bitmap = decodeUri(this, imageUri2!!)
                addBitmapToMemoryCache("bmp2",bitmap)
                Log.d("cekk", bitmap.toString())
                var rotatedBitmap: Bitmap? = null

                //check image rotation if image taken from camera
                imagePath2?.let {
                    bitmap?.let { it1 ->
                        rotatedBitmap = checkImageRotation(it, it1)
                    }
                }

                val stream = ByteArrayOutputStream()
                rotatedBitmap?.compress(Bitmap.CompressFormat.JPEG, 80, stream) ?: kotlin.run {
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                }
                val byteFormat: ByteArray = stream.toByteArray()

                // Get the Base64 string
                listImages.add(Base64.encodeToString(byteFormat, Base64.DEFAULT))
                var img1Encoded64 = Base64.encodeToString(byteFormat, Base64.DEFAULT)
                Log.d("cekk", "{${img1Encoded64.toString()}}")
                Log.d("cekk", "listImage ={${listImages.get(0).toString()}}")

            } catch (e: Exception){
                Log.e("cekk exception", "getEncoded64ImageStringFromBitmap: ${e.message}")
            }
        }

        imageUri3?.let {
            try {
                val bitmap = decodeUri(this, imageUri3!!)
                addBitmapToMemoryCache("bmp3",bitmap)
                var rotatedBitmap: Bitmap? = null

                //check image rotation if image taken from camera
                imagePath3?.let {
                    bitmap?.let { it1 ->
                        rotatedBitmap = checkImageRotation(it, it1)
                    }
                }

                val stream = ByteArrayOutputStream()
                rotatedBitmap?.compress(Bitmap.CompressFormat.JPEG, 80, stream) ?: kotlin.run {
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                }
                val byteFormat: ByteArray = stream.toByteArray()

                // Get the Base64 string
                listImages.add(Base64.encodeToString(byteFormat, Base64.DEFAULT))
            } catch (e: Exception){
                Log.e("cekk exception", "getEncoded64ImageStringFromBitmap: ${e.message}")
            }
        }
        return listImages
    }

    private fun decodeUri(c: Context, uri: Uri): Bitmap? {
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        var scale = 8

        BitmapFactory.decodeStream(c.contentResolver.openInputStream(uri), null, o)
        val o2 = BitmapFactory.Options()
        o2.inSampleSize = scale
        return BitmapFactory.decodeStream(c.contentResolver.openInputStream(uri), null, o2)
    }

    private fun getRealPathFromURI(contentURI: Uri): String? {
        var filePath = "no-path-found"
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(contentURI, filePathColumn, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                filePath = cursor.getString(columnIndex)
            }
        }
        cursor?.close()
        return filePath
    }

    private fun checkAndRequestPermissions(): Boolean {
        val permissionCam = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val permissionWriteEx = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val permissionReadEx = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (permissionWriteEx != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionReadEx != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissionCam != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                InputOrderActivity.REQ_MULTIPLE_PERMISSION
            )
            return false
        }
        return true
    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }

    private fun showAlertDialog(id: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.dialog_title_warning)
        builder.setMessage(R.string.dialog_delete_orderitem_detail)
        builder.setPositiveButton(R.string.dialog_backpressed_yes) { dialog, which ->
            val addedOrderItemDetailCount = intent.getIntExtra("addedOrderItemDetailCount", 0)
            if (addedOrderItemDetailCount == 1) {
                showMessage("Gagal menghapus. Item harus memiliki minimal 1 detail.")
            } else {
                val returnIntent = Intent().putExtra("removedId", id)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        }

        builder.setNegativeButton(R.string.dialog_backpressed_no) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
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

            if (binding.etOrderitemQty.text.isNotBlank() && binding.etOrderitemQty.text.toString()
                    .toInt() < 1
            ) {
                showMessage("Jumlah tidak boleh kurang dari 1")
            } else if (binding.etOrderitemQty.text.isNotBlank() && binding.etOrderitemQty.text.toString()
                    .toInt() > remainingQuantity
            ) {
                showMessage("Jumlah melebihi total item")
            } else if (binding.etOrderitemQty.text.isNotBlank()
//                && binding.etOrderitemDesc.text.isNotBlank()
            ) {
                if (binding.atOrderitemUcapan.visibility == View.VISIBLE) {
                    // is decorate item
                    if (binding.atOrderitemUcapan.text.isNotBlank()) {
                        //TODO : parse image to cache
                        val images = getEncoded64ImageStringFromBitmap()
                        val imagesUri = arrayListOf<Uri>()
                        imagesUri.clear()
                        imageUri1?.let { imagesUri.add(it) }
                        imageUri2?.let { imagesUri.add(it) }
                        imageUri3?.let { imagesUri.add(it) }
                        val result = if (imagesUri.isNotEmpty()) {
                            Log.d("cekk", "onOptionsItemSelected: with ${imagesUri.size} image")
                            OrderAddItemDetail(
                                quantity = binding.etOrderitemQty.text.toString().toInt(),
                                description = binding.etOrderitemDesc.text.toString(),
                                Decoration(
                                    ucapan = binding.atOrderitemUcapan.text.toString()
                                ),
                                images = images,
                                imagesUri = imagesUri
                            )
                        } else {
                            Log.d("cekk", "onOptionsItemSelected: without image")
                            OrderAddItemDetail(
                                quantity = binding.etOrderitemQty.text.toString().toInt(),
                                description = binding.etOrderitemDesc.text.toString(),
                                Decoration(
                                    ucapan = binding.atOrderitemUcapan.text.toString()
                                ),
                                images = null,
                                imagesUri = null
                            )
                        }

                        val returnIntent = Intent().putExtra("result", result)
                        if (orderItemData != null) {
                            returnIntent.putExtra("id", id)
                        }

                        // grant temporary permission to URI
                        if(images.isNotEmpty()){
                            imagesUri.forEach {
                                returnIntent.data = it
                            }
                            returnIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        }
                        try{
                            setResult(Activity.RESULT_OK, returnIntent)
                            finish()
                        }catch (exception : Exception){
                            Log.e("error","${exception.message.toString()}")
                        }
                    } else {
                        showMessage("Data belum lengkap")
                    }
                } else {
                    val result = OrderAddItemDetail(
                        quantity = binding.etOrderitemQty.text.toString().toInt(),
                        description = binding.etOrderitemDesc.text.toString(),
                        Decoration(
                            ucapan = binding.atOrderitemUcapan.text.toString()
                        )
                    )
                    val returnIntent = Intent().putExtra("result", result)

                    if (orderItemData != null) {
                        returnIntent.putExtra("id", id)
                    }

                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }

            } else {
                showMessage("Data belum lengkap")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == InputOrderActivity.REQ_MULTIPLE_PERMISSION) {
            val perms: MutableMap<String, Int> = HashMap()
            // Initialize the map with both permissions
            perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
            perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
            perms[Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
            // Fill with actual results from user
            if (grantResults.isNotEmpty()) {
                for (i in permissions.indices) perms[permissions[i]] = grantResults[i]
                // Check for permissions
                if (perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED
                    && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                    && perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d("RequestPermission", "sms & location services permission granted")
                    // process the normal flow
                    //else any one or both the permissions are not granted
                } else {
                    Log.d("RequestPermission", "Some permissions are not granted ask again ")
                    //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                    // shouldShowRequestPermissionRationale will return true
                    //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.CAMERA
                        ) || ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) || ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    ) {
                        showDialogOK("Camera and External Storage Permission are required for this app") { _, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                DialogInterface.BUTTON_NEGATIVE -> {
                                }
                            }
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Go to settings and enable permissions",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_IMG_DECOR_1 && resultCode == Activity.RESULT_OK) {
            val isCamera: Boolean
            if (data == null) {
                isCamera = true
                tempImageUri1?.let { imagePath1 = it.path }
            } else {
                isCamera = MediaStore.ACTION_IMAGE_CAPTURE == data?.action
                data.data.let {
                    it?.let { uri -> imagePath1 = getRealPathFromURI(uri) }
                }
            }
            Log.d("cekk", "onActivityResult: real path 1 $imagePath1")
            val selectedImageUri: Uri? = if (isCamera) {
                tempImageUri1
            } else {
                data?.data
            }

            binding.imgDecor1.setImageURI(selectedImageUri)
            binding.btnAddDecor1.visibility = View.INVISIBLE
            binding.imgDecor1.visibility = View.VISIBLE
            binding.btnChangeImg1.visibility = View.VISIBLE
            binding.btnRemoveImg1.visibility = View.VISIBLE
            imageUri1 = selectedImageUri
            imageBitmap1 = null
        } else if (requestCode == ADD_IMG_DECOR_2 && resultCode == Activity.RESULT_OK) {
            val isCamera: Boolean
            if (data == null) {
                isCamera = true
                tempImageUri2?.let { imagePath2 = it.path }
            } else {
                isCamera = MediaStore.ACTION_IMAGE_CAPTURE == data?.action
                data.data.let {
                    it?.let { uri -> imagePath2 = getRealPathFromURI(uri) }
                }
            }
            Log.d("cekk", "onActivityResult: real path 2 $imagePath2")
            val selectedImageUri: Uri? = if (isCamera) {
                tempImageUri2
            } else {
                data?.data
            }

            binding.imgDecor2.setImageURI(selectedImageUri)
            binding.btnAddDecor2.visibility = View.INVISIBLE
            binding.imgDecor2.visibility = View.VISIBLE
            binding.btnChangeImg2.visibility = View.VISIBLE
            binding.btnRemoveImg2.visibility = View.VISIBLE
            imageUri2 = selectedImageUri
            imageBitmap2 = null
        } else if (requestCode == ADD_IMG_DECOR_3 && resultCode == Activity.RESULT_OK) {
            val isCamera: Boolean
            if (data == null) {
                isCamera = true
                tempImageUri3?.let { imagePath3 = it.path }
            } else {
                isCamera = MediaStore.ACTION_IMAGE_CAPTURE == data?.action
                data.data.let {
                    it?.let { uri -> imagePath3 = getRealPathFromURI(uri) }
                }
            }
            Log.d("cekk", "onActivityResult: real path 2 $imagePath3")
            val selectedImageUri: Uri? = if (isCamera) {
                tempImageUri3
            } else {
                data?.data
            }

            binding.imgDecor3.setImageURI(selectedImageUri)
            binding.btnAddDecor3.visibility = View.INVISIBLE
            binding.imgDecor3.visibility = View.VISIBLE
            binding.btnChangeImg3.visibility = View.VISIBLE
            binding.btnRemoveImg3.visibility = View.VISIBLE
            imageUri3 = selectedImageUri
            imageBitmap3 = null
        }
    }
}