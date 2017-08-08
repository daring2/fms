package com.gitlab.daring.fms.common.network

import java.net.ServerSocket

open class ServerSocketProvider {

    open fun createServerSocket(port: Int): ServerSocket {
        return ServerSocket(port)
    }

}