package com.atiga.cakeorder.core.utils

import com.atiga.cakeorder.core.domain.model.DeleteItem
import com.atiga.cakeorder.core.domain.model.category.Category
import com.atiga.cakeorder.core.domain.model.dekor.JobDekor
import com.atiga.cakeorder.core.domain.model.order.*
import com.atiga.cakeorder.core.domain.model.product.Product
import com.atiga.cakeorder.core.domain.model.subcategory.EditSubCategory
import com.atiga.cakeorder.core.domain.model.subcategory.SubCategory
import com.atiga.cakeorder.core.domain.model.track.Tracking
import com.atiga.cakeorder.core.network.response.*

object DataMapper {
    fun mapListCategoryResponseToDomain(input: List<CategoryResponseItem>): List<Category> =
        input.map {
            Category(
                id = it.idKategori,
                name = it.namaKategori
            )
        }

    fun mapCategoryResponseToDomain(input: CategoryResponseItem): Category =
        Category(
            id = input.idKategori,
            name = input.namaKategori
        )

    fun mapCategoryDomainToResponse(input: Category): CategoryResponseItem =
        CategoryResponseItem(
            idKategori = input.id,
            namaKategori = input.name
        )

    fun mapListSubCategoryResponseToDomain(input: List<SubCategoryResponseItem>): List<SubCategory> =
        input.map {
            SubCategory(
                id = it.idSubKategori,
                name = it.namaSubKategori,
                maxOrder = it.maxOrder
            )
        }

    fun mapSubCategoryResponseToDomain(input: SubCategoryResponseItem): SubCategory =
        SubCategory(
            id = input.idSubKategori,
            name = input.namaSubKategori,
            maxOrder = input.maxOrder
        )

    fun mapSubCategoryDomainToResponse(input: SubCategory): SubCategoryResponseItem =
        SubCategoryResponseItem(
            idSubKategori = input.id,
            namaSubKategori = input.name,
            maxOrder = input.maxOrder
        )

    fun mapEditSubCategoryResponseToDomain(input: EditSubCategoryResponse): EditSubCategory =
        EditSubCategory(
            idSubKategori = input.idSubKategori,
            namaSubKategori = input.namaSubKategori,
        )

    fun mapDeleteResponseToDomain(input: DeleteResponse): DeleteItem =
        DeleteItem(
            isSuccess = input.isSuccessDelete,
            message = input.message
        )

    fun mapListProductResponseToDomain(input: List<ProductResponse>): List<Product> =
        input.map {
            Product(
                id = it.idBarang,
                idSubCategory = it.idSubKategori,
                name = it.namaBarang,
                isDecorate = it.dekorasi,
                description = it.deskripsiBarang,
                gambarProduk = it.gambarProduk,
                idKategori = it.idKategori
            )
        }

    fun mapProductResponseToDomain(input: ProductResponse): Product =
        Product(
            id = input.idBarang,
            idSubCategory = input.idSubKategori,
            name = input.namaBarang,
            isDecorate = input.dekorasi,
            description = input.deskripsiBarang,
            gambarProduk = input.gambarProduk,
            idKategori = input.idKategori
        )

    fun mapProductDomainToResponse(input: Product): ProductResponse =
        ProductResponse(
            idBarang = input.id,
            idSubKategori = input.idSubCategory,
            namaBarang = input.name,
            deskripsiBarang = input.description,
            dekorasi = input.isDecorate,
            gambarProduk = input.gambarProduk,
            idKategori = input.idKategori
        )

    fun mapListOrderResponseToDomain(input: List<OrderResponse>): List<Order> {
        return input.map {
            val listItem = mutableListOf<ItemOrder>()
            it.items?.forEach { item ->
                item.dekorasi?.ucapan?.let { decor ->
                    listItem.add(
                        ItemOrder(
                            idBarang = item.idBarang,
                            keterangan = item.keterangan,
                            jumlah = item.jumlah,
                            flag = item.flag,
                            dekorasi = Decoration(decor),
                            finishDate = item.tanggalSelesai,
                            namaBarang = item.namaBarang,
                            noPesanan = item.noPesanan,
                            itemKey = item.itemKey,
                            jobStarted = item.jobStarted,
                            urutKey = item.urutKey,
                            hasBeenCanceled = item.hasBeenCanceled,
                            pickupDate = item.tanggalPickup,
                            images = item.images,
                        )
                    )
                } ?: run {
                    listItem.add(
                        ItemOrder(
                            idBarang = item.idBarang,
                            keterangan = item.keterangan,
                            jumlah = item.jumlah,
                            flag = item.flag,
                            dekorasi = null,
                            finishDate = item.tanggalSelesai,
                            namaBarang = item.namaBarang,
                            noPesanan = item.noPesanan,
                            itemKey = item.itemKey,
                            jobStarted = item.jobStarted,
                            urutKey = item.urutKey,
                            hasBeenCanceled = item.hasBeenCanceled,
                            images = item.images,
                            pickupDate = item.tanggalPickup
                        )
                    )
                }
            }

            Order(
                orderNumber = it.noPesanan,
                phoneNumber = it.noTelponPemesan,
                customerName = it.namaPemesan,
                isPaid = it.isLunas,
                downPayment = it.downPayment,
                items = listItem,
                orderDate = it.tanggalPesanan,
                tanggalAmbil = it.tanggalAmbil,
            )
        }
    }

    fun mapOrderResponseToDomain(input: OrderResponse): Order {
        val listItem = mutableListOf<ItemOrder>()
        input.items?.forEach {
            it.dekorasi?.ucapan?.let { decor ->
                listItem.add(
                    ItemOrder(
                        idBarang = it.idBarang,
                        keterangan = it.keterangan,
                        jumlah = it.jumlah,
                        flag = it.flag,
                        dekorasi = Decoration(decor),
                        finishDate = it.tanggalSelesai,
                        namaBarang = it.namaBarang,
                        noPesanan = it.noPesanan,
                        itemKey = it.itemKey,
                        jobStarted = it.jobStarted,
                        urutKey = it.urutKey,
                        hasBeenCanceled = it.hasBeenCanceled,
                        images = it.images,
                        pickupDate = it.tanggalPickup
                    )
                )
            } ?: run {
                listItem.add(
                    ItemOrder(
                        idBarang = it.idBarang,
                        keterangan = it.keterangan,
                        jumlah = it.jumlah,
                        flag = it.flag,
                        dekorasi = null,
                        finishDate = it.tanggalSelesai,
                        namaBarang = it.namaBarang,
                        noPesanan = it.noPesanan,
                        itemKey = it.itemKey,
                        jobStarted = it.jobStarted,
                        urutKey = it.urutKey,
                        hasBeenCanceled = it.hasBeenCanceled,
                        images = it.images,
                        pickupDate = it.tanggalPickup
                    )
                )
            }
        }

        return Order(
            orderNumber = input.noPesanan,
            phoneNumber = input.noTelponPemesan,
            customerName = input.namaPemesan,
            isPaid = input.isLunas,
            downPayment = input.downPayment,
            items = listItem,
            orderDate = input.tanggalPesanan,
            tanggalAmbil = input.tanggalAmbil
        )
    }

    fun mapFinishOrderResponseToDomain(input: FinishOrderResponse) =
        FinishOrder(
            input.isSuccessFinishingOrder,
            input.message
        )

    fun mapListCustomerResponseToDomain(input: List<CustomerResponse>): List<Customer> =
        input.map {
            Customer(
                name = it.namaPemesan,
                phoneNumber = it.noTelponPemesan
            )
        }

    fun mapListItemOrderResponseToDomain(input: List<ItemOrderResponse>): List<ItemOrder> {
        val listItem = mutableListOf<ItemOrder>()
        input.forEach {
            it.dekorasi?.ucapan?.let { decor ->
                listItem.add(
                    ItemOrder(
                        idBarang = it.idBarang,
                        keterangan = it.keterangan,
                        jumlah = it.jumlah,
                        flag = it.flag,
                        dekorasi = Decoration(decor),
                        finishDate = it.tanggalSelesai,
                        itemKey = it.itemKey,
                        namaBarang = it.namaBarang,
                        noPesanan = it.noPesanan,
                        jobStarted = it.jobStarted,
                        urutKey = it.urutKey,
                        hasBeenCanceled = it.hasBeenCanceled,
                        images = it.images,
                        pickupDate = it.tanggalPickup
                    )
                )
            } ?: run {
                listItem.add(
                    ItemOrder(
                        idBarang = it.idBarang,
                        keterangan = it.keterangan,
                        jumlah = it.jumlah,
                        flag = it.flag,
                        dekorasi = null,
                        finishDate = it.tanggalSelesai,
                        itemKey = it.itemKey,
                        namaBarang = it.namaBarang,
                        noPesanan = it.noPesanan,
                        jobStarted = it.jobStarted,
                        urutKey = it.urutKey,
                        hasBeenCanceled = it.hasBeenCanceled,
                        pickupDate = it.tanggalPickup
                    )
                )
            }
        }
        return listItem
    }

    fun mapItemOrderResponseToDomain(input: ItemOrderResponse): ItemOrder {
        return input.dekorasi?.ucapan?.let {
            ItemOrder(
                idBarang = input.idBarang,
                keterangan = input.keterangan,
                jumlah = input.jumlah,
                flag = input.flag,
                dekorasi = Decoration(it),
                finishDate = input.tanggalSelesai,
                itemKey = input.itemKey,
                namaBarang = input.namaBarang,
                noPesanan = input.noPesanan,
                jobStarted = input.jobStarted,
                urutKey = input.urutKey,
                hasBeenCanceled = input.hasBeenCanceled,
                images = input.images,
                pickupDate = input.tanggalPickup
            )
        } ?: run {
            ItemOrder(
                idBarang = input.idBarang,
                keterangan = input.keterangan,
                jumlah = input.jumlah,
                flag = input.flag,
                dekorasi = null,
                finishDate = input.tanggalSelesai,
                itemKey = input.itemKey,
                namaBarang = input.namaBarang,
                noPesanan = input.noPesanan,
                jobStarted = input.jobStarted,
                urutKey = input.urutKey,
                hasBeenCanceled = input.hasBeenCanceled,
                pickupDate = input.tanggalPickup
            )
        }

    }

    fun mapJobDekorResponseToDomain(input: JobDekorResponse): JobDekor =
        JobDekor(
            input.isSuccessStarted,
            input.message
        )

    fun mapListTrackingResponseToDomain(input: List<TrackingResponse>): List<Tracking> =
        input.map {
            Tracking(
                it.trackingDate,
                it.trackingStatus,
                it.trackingActiveStatus
            )
        }
}