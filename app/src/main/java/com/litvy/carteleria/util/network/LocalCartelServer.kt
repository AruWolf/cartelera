package com.litvy.carteleria.util.network

import android.content.Context
import com.litvy.carteleria.domain.server.CartelServer

class LocalCartelServer(
    private val context: Context
) : CartelServer {

    private val server = LocalHttpServer(context, 8080)

    override fun start() {
        server.start()
    }

    override fun stop() {
        server.stop()
    }

    override fun getUrl(): String {
        val ip = getLocalIpAddress()
        return "http://$ip:8080"
    }
}