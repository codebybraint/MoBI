package com.atiga.cakeorder.core.network.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

class KitchenOrderResponse : ArrayList<KitchenOrderResponseItem>()

@Parcelize
class KitchenOrderResponseItem(
    val dekorasi: Dekorasi? = null,
    val detail: Detail,
    val flag: Boolean,
    val hasBeenCanceled: Boolean,
    val idBarang: Int,
    val images: List<String>? = null,
    val itemKey: Int,
    val jobStarted: Boolean,
    val jumlah: Int,
    val keterangan: String,
    val namaBarang: String,
    val noPesanan: String,
    val tanggalPickup: String? = null,
    val tanggalSelesai: String? = null,
    val tglAmbil: String? = null,
    val urutKey: Int
): Parcelable

@Parcelize
class Detail(
    val tglMulai: String? = null,
    val tglSelesai: String? = null,
    val usrKey: String? = null,
    val usrName: String? = null
): Parcelable

@Parcelize
class Dekorasi(
    val ucapan: String
): Parcelable