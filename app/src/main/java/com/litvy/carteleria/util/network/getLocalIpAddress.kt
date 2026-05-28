package com.litvy.carteleria.util.network

import java.net.Inet4Address
import java.net.NetworkInterface
import kotlin.collections.iterator

fun getLocalIpAddress(): String {
    val interfaces = NetworkInterface.getNetworkInterfaces()
    for (intf in interfaces) {
        for (addr in intf.inetAddresses) {
            if (!addr.isLoopbackAddress && addr is Inet4Address) {
                return addr.hostAddress ?: ""
            }
        }
    }
    return ""
}