package com.atiga.cakeorder.core.domain.enum

enum class ReportType(val id: Int) {
    ORDER(1),
    PICKED_ORDER(2),
    UNPICKED_ORDER(3),
    FINISHED_ORDER(4),
    UNFINISHED_ORDER(5)
}