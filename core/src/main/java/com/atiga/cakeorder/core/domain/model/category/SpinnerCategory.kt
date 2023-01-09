package com.atiga.cakeorder.core.domain.model.category

data class SpinnerCategory(
    var id: Int = 0,
    var name: String = ""
) {
    override fun toString(): String {
        return name
    }
}