package com.atiga.cakeorder.core.domain.model.report

class Report(
    var reportTypeId: Int,
    var startDate: String,
    var endDate: String,
    var kategoriId: Int? = null,
    var subKategoriId: Int? = null,
    var variantProduct: Boolean? = null
)