package com.airasoft.banregio.model

data class PrestamosModel(
    val cliente: String,
    val id: Int,
    val fecha: String,
    val monto: Double,
    val estado: Enum<Estado>
)
