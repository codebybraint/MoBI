package com.atiga.cakeorder.core.domain.model.order

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Order(
    var orderNumber: String? = null,
    var phoneNumber: String? = null,
    var isPaid: Boolean? = null,
    val downPayment: Double? = null,
    val items: List<ItemOrder>,
    val customerName: String? = null,
    var orderDate: String? = null,
    var tanggalAmbil: String? = null,
    var pickupDate: String? = null,

): Parcelable {
    override fun toString(): String {
        return "Order(items=$items)"
    }
}

class AddOrder(
    var noTelponPemesan: String? = null,
    var isLunas: Boolean? = null,
    val downPayment: Double? = null,
    val items: List<ItemOrder?>? = null,
    val namaPemesan: String? = null,
    var tanggalAmbil: String? = null,
)

@Parcelize
class ItemOrder(
    var idBarang: Int,
    var keterangan: String? = null,
    var dekorasi: Decoration? = null,
    var jumlah: Int,
    val flag: Boolean? = null,
    var isDecorate: Boolean? = null,
    var finishDate: String? = null,
    var namaBarang: String? = null,
    var noPesanan: String? = null,
    var itemKey: Int? = null,
    var jobStarted: Boolean? = null,
    var urutKey: Int? = null,
    var hasBeenCanceled: Boolean? = null,
    var images: List<String>? = null,
    var pickupDate: String? = null,
): Parcelable {
    override fun toString(): String {
        return "ItemOrder(images=$images)"
    }
}

@Parcelize
class Decoration(
    var ucapan: String? = null
): Parcelable