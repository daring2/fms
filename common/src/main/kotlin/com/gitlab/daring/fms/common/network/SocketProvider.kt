package com.gitlab.daring.fms.common.network

import java.net.Socket

interface SocketProvider {

    fun createSocket(host: String?, port: Int): Socket
    
}