package com.atiga.cakeorder.core.network.response

class ProductResponse(
    val idBarang: Int,
    val namaBarang: String,
    val deskripsiBarang: String,
    val dekorasi: Boolean,
    val idSubKategori: Int,
    val gambarProduk: String? = null,
    val idKategori: Int? = null
)