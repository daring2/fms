package com.gitlab.daring.fms.zabbix.util

import com.gitlab.daring.fms.common.network.SocketProvider
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import java.io.ByteArrayOutputStream
import java.net.Socket
import java.nio.charset.Charset

class MockSocketProvider: SocketProvider {

    val provider: SocketProvider = mock(SocketProvider::class.java)
    val socket: Socket = mock(Socket::class.java)
    val output = createOutput()

    init {
        `when`(provider.createSocket(any(), anyInt())).thenReturn(socket)
    }

    fun createOutput(): ByteArrayOutputStream {
        val stream = ByteArrayOutputStream()
        `when`(socket.getOutputStream()).thenReturn(stream)
        return stream
    }

    fun setInput(str: String, charset: Charset = Charsets.UTF_8) {
        val stream = str.byteInputStream(charset)
        `when`(socket.getInputStream()).thenReturn(stream)
    }

    override fun createSocket(host: String?, port: Int): Socket {
        return provider.createSocket(host, port)
    }

}