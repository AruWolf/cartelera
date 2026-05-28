package com.litvy.carteleria.domain.server

interface CartelServer {
    fun start()
    fun stop()
    fun getUrl(): String
}