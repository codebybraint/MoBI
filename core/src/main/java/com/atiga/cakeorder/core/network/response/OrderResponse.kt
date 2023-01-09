package com.atiga.cakeorder.core.network.response

class OrderResponse(
	val noPesanan: String? = null,
	val noTelponPemesan: String? = null,
	val isLunas: Boolean? = null,
	val downPayment: Double? = null,
	val items: List<ItemOrderResponse>? = null,
	val namaPemesan: String? = null,
	val tanggalPesanan: String? = null,
	val tanggalAmbil: String? = null,
//	val tanggalPickup: String? = null,
) {
	override fun toString(): String {
		return "OrderResponse(items=${items.toString()})"
	}
}

data class ItemOrderResponse(
	val idBarang: Int,
	val keterangan: String? = null,
	val dekorasi: DekorasiResponse? = null,
	val jumlah: Int,
	val flag: Boolean? = null,
	val tanggalSelesai: String? = null,
	var namaBarang: String? = null,
	var noPesanan: String? = null,
	var itemKey: Int? = null,
	var jobStarted: Boolean? = null,
	var urutKey: Int? = null,
	val hasBeenCanceled: Boolean? = null,
	val images: List<String>? = null,
	val tanggalPickup: String? = null,
) {
	override fun toString(): String {
		return "ItemOrderResponse(images=$images)"
	}
}

data class DekorasiResponse(
	val ucapan: String? = null
)
