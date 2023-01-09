package com.atiga.cakeorder.core.domain.model.product

class AddProduct(
    var namaBarang: String,
    var deskripsiBarang: String,
    var dekorasi: Boolean,
    var idSubKategori: Int,
    val gambarProduk: String? = null
)