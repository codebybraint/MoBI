package com.atiga.cakeorder.core.domain.model.order

class CancelOrder(
    var noPesanan: String,
    var itemKey: Int,
    var namaBatal: String,
    var alasan: String
)