package com.gitlab.daring.fms.common.network

import com.gitlab.daring.fms.common.config.getMillis
import com.typesafe.config.Config
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import javax.net.SocketFactory

/**
 * Implementation of [SocketProvider] and [SocketFactory] with configurable timeouts.
 */
open class SocketProviderImpl(
        val connectTimeout: Int,
        val readTimeout: Int
): SocketFactory(), SocketProvider {

    constructor(c: Config):  this(
            c.getMillis("connectTimeout").toInt(),
            c.getMillis("readTimeout").toInt()
    )

    override fun createSocket(host: String?, port: Int): Socket {
        return createSocket(newSocketAddress(host, port), null)
    }

    override fun createSocket(address: InetAddress?, port: Int): Socket {
        address ?: NullPointerException("address")
        return createSocket(InetSocketAddress(address, port), null)
    }

    override fun createSocket(host: String?, port: Int, localAddress: InetAddress?, localPort: Int): Socket {
        return createSocket(newSocketAddress(host, port), InetSocketAddress(localAddress, localPort))
    }

    override fun createSocket(address: InetAddress?, port: Int, localAddress: InetAddress?, localPort: Int): Socket {
        address ?: NullPointerException("address")
        return createSocket(InetSocketAddress(address, port), InetSocketAddress(localAddress, localPort))
    }

    protected fun createSocket(address: SocketAddress, localAddress: SocketAddress?): Socket {
        val socket = newSocket()
        socket.soTimeout = readTimeout
        if (localAddress != null)
            socket.bind(localAddress)
        socket.connect(address, connectTimeout)
        return socket
    }

    protected open fun newSocket() = Socket()

    protected fun newSocketAddress(host: String?, port: Int): InetSocketAddress {
        return if (host != null) {
            InetSocketAddress(host, port)
        } else {
            InetSocketAddress(InetAddress.getByName(null), port)
        }
    }

}