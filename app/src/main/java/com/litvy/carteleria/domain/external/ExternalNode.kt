package com.litvy.carteleria.domain.external

sealed class ExternalNode {
    data class Folder(val folder: ExternalFolder) : ExternalNode()
    data class File(val file: ExternalFile) : ExternalNode()
}