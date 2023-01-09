package com.atiga.cakeorder.core.domain.model.order

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class OrderAddItem(
    var idProduct: Int,
    var productName: String,
    var totalQuantity: Int,
    var listOrderItem: ArrayList<OrderAddItemDetail>,
): Parcelable

@Parcelize
class OrderAddItemDetail(
    var quantity: Int,
    var description: String,
    var decoration: Decoration? = null,
    var images: List<String>? = null,
    var imagesUri: List<Uri>? = null
): Parcelable