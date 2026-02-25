package com.litvy.carteleria.domain.external

data class ExternalFile(
    val name: String,
    val path: String,
    val isHidden: Boolean = false
)
