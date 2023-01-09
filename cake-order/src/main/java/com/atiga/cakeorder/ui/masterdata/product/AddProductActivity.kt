package com.atiga.cakeorder.ui.masterdata.product

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import com.atiga.cakeorder.R
import com.atiga.cakeorder.core.domain.model.category.SpinnerCategory
import com.atiga.cakeorder.core.domain.model.product.AddProduct
import com.atiga.cakeorder.core.domain.model.product.Product
import com.atiga.cakeorder.databinding.ActivityAddProductBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream


class AddProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProductBinding
    private val addProductViewModel: AddProductViewModel by viewModel()
    private var subCategoryId = 0
    private var isEditProduct = false
    private var productData: Product? = null
    private var productId = 0
    private lateinit var categoryAdapter: ArrayAdapter<SpinnerCategory>
    private lateinit var subCategoryAdapter: ArrayAdapter<SpinnerCategory>
    private var selectedCategoryId = 0
    private var selectedSubCategoryId = 0
    private var imageUri: Uri? = null
    private var imageBitmap: Bitmap? = null

    companion object {
        const val PICK_IMAGE = 1;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Produk"

        fetchData()

        binding.btnDeleteProduct.setOnClickListener {
            showAlertDialog(productId)
        }

        addProductViewModel.isDeleted.observe(this, {
            it?.let {
                isLoading(stat = false, isDelete = true)
                Toast.makeText(this, "Produk berhasil dihapus", Toast.LENGTH_SHORT).show()
                val returnIntent = Intent()
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        })

        addProductViewModel.isImageDeleted.observe(this, {
            it?.let { result ->
                if (!result.isSuccess) {
                    showMessage("Gambar gagal dihapus")
                }
            }
        })

        addProductViewModel.error.observe(this, {
            it?.let {
                showMessage(it)
            }
        })

        binding.btnAddProductImage.setOnClickListener {
            val gallery = Intent()
            gallery.type = "image/*"
            gallery.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(gallery, PICK_IMAGE)
        }

        binding.btnChangeProductImg.setOnClickListener {
            val gallery = Intent()
            gallery.type = "image/*"
            gallery.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(gallery, PICK_IMAGE)
        }

        addProductViewModel.category.observe(this, {
            it?.let {
                val arrayCategory = arrayListOf<SpinnerCategory>()
                it.forEach { category ->
                    arrayCategory.add(SpinnerCategory(category.id, category.name))
                }

                categoryAdapter =
                    ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayCategory)
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerCategory.adapter = categoryAdapter
                selectedCategoryId = arrayCategory[0].id
            }
        })

        addProductViewModel.subCategory.observe(this, {
            it?.let {
                val arraySubCategory = arrayListOf<SpinnerCategory>()
                it.forEach { subCategory ->
                    arraySubCategory.add(SpinnerCategory(subCategory.id, subCategory.name))
                }

                subCategoryAdapter =
                    ArrayAdapter(this, android.R.layout.simple_spinner_item, arraySubCategory)
                subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerSubcategory.adapter = subCategoryAdapter
                selectedSubCategoryId = arraySubCategory[0].id
            }
        })

        binding.spinnerCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val selectedCategory = binding.spinnerCategory.selectedItem as SpinnerCategory
                    fetchSubCategory(selectedCategory.id)
                    selectedCategoryId = selectedCategory.id
                }
            }

        binding.spinnerSubcategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val selectetSubCategory =
                        binding.spinnerSubcategory.selectedItem as SpinnerCategory
                    selectedSubCategoryId = selectetSubCategory.id
                }
            }

        binding.btnSelect.setOnClickListener {
            if (selectedCategoryId != 0 && selectedSubCategoryId != 0) {
                binding.cardInputProductDetail.visibility = View.VISIBLE
                Log.d(
                    "cekid",
                    "onCreate: categoryId=$selectedCategoryId subcategoryId=$selectedSubCategoryId"
                )
            }
        }

        binding.imgProductPreview.setOnClickListener {
            showImageDialog()
        }

        binding.btnRemoveProductImg.setOnClickListener {
            imageUri = null
            imageBitmap = null
            binding.btnAddProductImage.visibility = View.VISIBLE
            binding.imgProductPreview.visibility = View.INVISIBLE
            binding.btnChangeProductImg.visibility = View.INVISIBLE
            binding.btnRemoveProductImg.visibility = View.INVISIBLE
        }
    }

    private fun fetchData() {
        subCategoryId = intent.getIntExtra("id", 0)
        productData = intent.getParcelableExtra<Product>("productData")

        when {
            productData != null -> {
                isEditProduct = true

                productData?.let {
                    binding.cbProductDecorate.isChecked = it.isDecorate
                    binding.etProductName.setText(it.name)
                    binding.etProductDesc.setText(it.description)
                    subCategoryId = it.idSubCategory
                    productId = it.id
                    if (!it.gambarProduk.isNullOrBlank()) {
                        val imageByteArray = Base64.decode(it.gambarProduk, Base64.DEFAULT)
                        imageBitmap = BitmapFactory.decodeByteArray(
                            imageByteArray,
                            0,
                            imageByteArray.size
                        )
                        Glide.with(this).load(imageByteArray).centerCrop()
                            .into(binding.imgProductPreview)
                        binding.btnAddProductImage.visibility = View.INVISIBLE
                        binding.imgProductPreview.visibility = View.VISIBLE
                        binding.btnChangeProductImg.visibility = View.VISIBLE
                        binding.btnRemoveProductImg.visibility = View.VISIBLE
                    }
                }

                binding.btnDeleteProduct.visibility = View.VISIBLE
                binding.cardChooseCategory.visibility = View.GONE
            }
            subCategoryId == 0 -> {
                binding.cardChooseCategory.visibility = View.VISIBLE
                binding.cardInputProductDetail.visibility = View.GONE

                fetchAllCategory()
            }
            else -> {
                binding.cardChooseCategory.visibility = View.GONE
            }
        }
    }

    private fun fetchAllCategory() {
        lifecycleScope.launch {
            addProductViewModel.getCategory()
        }
    }

    private fun fetchSubCategory(id: Int) {
        lifecycleScope.launch {
            addProductViewModel.getSubCategoryByCategoryId(id)
        }
    }

    private fun getEncoded64ImageStringFromBitmap(): String? {
        imageUri?.let {
//            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            val bitmap = decodeUri(this, imageUri!!)
            var rotatedBitmap: Bitmap? = null

            val stream = ByteArrayOutputStream()
            bitmap?.compress(CompressFormat.JPEG, 80, stream)
            val byteFormat: ByteArray = stream.toByteArray()

            // Get the Base64 string
            return Base64.encodeToString(byteFormat, Base64.DEFAULT)
        } ?: kotlin.run {
            return null
        }
    }

    private fun decodeUri(c: Context, uri: Uri): Bitmap? {
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        // BitmapFactory.decodeStream(c.contentResolver.openInputStream(uri), null, o)
        // var width = o.outWidth
        // var height = o.outHeight
        // var scale = 1

        // if(width >= 3000 || height >= 3000) {
        //     width /= 5
        //     height /= 5
        //     scale *= 5
        // } else if(width >= 2000 || height >= 2000) {
        //     width /= 4
        //     height /= 4
        //     scale *= 4
        // } else if(width >= 1000 || height >= 1000) {
        //     width /= 2
        //     height /= 2
        //     scale *= 2
        // }

        var scale = 8
        BitmapFactory.decodeStream(c.contentResolver.openInputStream(uri),null, o)

        val o2 = BitmapFactory.Options()
        o2.inSampleSize = scale
        return BitmapFactory.decodeStream(c.contentResolver.openInputStream(uri), null, o2)
    }

    private fun showAlertDialog(id: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.dialog_title_warning)
        builder.setMessage(R.string.dialog_delete_product_message)
        builder.setPositiveButton(R.string.dialog_backpressed_yes) { dialog, _ ->
            isLoading(stat = true, isDelete = true)
            lifecycleScope.launch {
                addProductViewModel.deleteProduct(id)
            }
        }

        builder.setNegativeButton(R.string.dialog_backpressed_no) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun showImageDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_image_preview)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val imgView = dialog.findViewById<ImageView>(R.id.img_preview)
        imageUri?.let {
            imgView.setImageURI(imageUri)
        } ?: run {
            imgView.setImageBitmap(imageBitmap)
        }

        dialog.findViewById<ImageButton>(R.id.btn_close_preview).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun isLoading(stat: Boolean, isDelete: Boolean) {
        if (stat) {
            binding.progressBarUpdate.visibility = View.VISIBLE
            if (isDelete) {
                binding.btnDeleteProduct.isEnabled = false
            }
        } else {
            binding.progressBarUpdate.visibility = View.GONE
            if (isDelete) {
                binding.btnDeleteProduct.isEnabled = true
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_submit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        } else if (item.itemId == R.id.action_submit) {
            val productImageBase64 = getEncoded64ImageStringFromBitmap()
            if (!isEditProduct && binding.etProductDesc.text.isNotBlank() && binding.etProductName.text.isNotBlank()) {
                item.isEnabled = false
                isLoading(stat = true, isDelete = false)

                if (subCategoryId == 0) subCategoryId = selectedSubCategoryId

                lifecycleScope.launch {
                    addProductViewModel.addProduct(
                        AddProduct(
                            binding.etProductName.text.toString(),
                            binding.etProductDesc.text.toString(),
                            binding.cbProductDecorate.isChecked,
                            subCategoryId,
                            productImageBase64
                        )
                    )
                }

                addProductViewModel.addedProduct.observe(this, {
                    it?.let {
                        showMessage("Produk berhasil ditambahkan")
                        val returnIntent = Intent()
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }
                })
            } else if (isEditProduct && binding.etProductDesc.text.isNotBlank() && binding.etProductName.text.isNotBlank()) {
                Log.d("cekprod", "onOptionsItemSelected: edit")
                item.isEnabled = false
                isLoading(stat = true, isDelete = false)

                lifecycleScope.launch {
                    addProductViewModel.editProduct(
                        productId,
                        Product(
                            id = productId,
                            idSubCategory = subCategoryId,
                            name = binding.etProductName.text.toString(),
                            description = binding.etProductDesc.text.toString(),
                            isDecorate = binding.cbProductDecorate.isChecked,
                            gambarProduk = productImageBase64
                        )
                    )

                    if (imageUri == null && imageBitmap == null) {
                        addProductViewModel.deleteProductImage(productId)
                    }
                }

                addProductViewModel.editedProduct.observe(this, {
                    it?.let {
                        showMessage("Produk berhasil disimpan")
                        val returnIntent = Intent()
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }
                })
            } else {
                showMessage("Data belum lengkap")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data?.data
            binding.imgProductPreview.setImageURI(imageUri)
            binding.btnAddProductImage.visibility = View.INVISIBLE
            binding.imgProductPreview.visibility = View.VISIBLE
            binding.btnChangeProductImg.visibility = View.VISIBLE
            binding.btnRemoveProductImg.visibility = View.VISIBLE
        }
    }
}
