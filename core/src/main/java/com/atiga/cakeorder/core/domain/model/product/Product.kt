package com.atiga.cakeorder.core.domain.model.product

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: Int,
    val idSubCategory: Int,
    val name: String,
    val isDecorate: Boolean,
    var description: String,
    val image: String? = null,
    var addAmount: Int = 1,
    var pickupDate: String? = null,
    var addedSequence: Int = 1,
    var gambarProduk: String? = null,
    val idKategori: Int? = null
): Parcelable
