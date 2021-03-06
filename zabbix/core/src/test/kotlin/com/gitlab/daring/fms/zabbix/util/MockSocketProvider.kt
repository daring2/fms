package com.gitlab.daring.fms.zabbix.util

import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import com.gitlab.daring.fms.common.network.ServerSocketProvider
import com.gitlab.daring.fms.common.network.SocketProvider
import org.awaitility.Awaitility.await
import org.junit.Assert.assertArrayEquals
import org.mockito.Mockito.*
import java.io.ByteArrayOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.concurrent.LinkedBlockingQueue

@Suppress("HasPlatformType")
class MockSocketProvider : AutoCloseable {

    val provider = mock(SocketProvider::class.java)
    var socket = newSocket()

    val serverProvider = mock(ServerSocketProvider::class.java)
    val serverSocket = mock(ServerSocket::class.java)
    val acceptQueue = LinkedBlockingQueue<Socket>()

    init {
        `when`(provider.createSocket(any(), anyInt())).thenAnswer { socket }
        `when`(serverProvider.createServerSocket(anyInt())).thenReturn(serverSocket)
        `when`(serverSocket.accept()).thenAnswer { acceptQueue.take() }
        `when`(serverSocket.close()).then {
            `when`(serverSocket.isClosed).thenReturn(true)
        }
    }

    fun newSocket(): Socket {
        socket = mock(Socket::class.java)
        `when`(socket.getOutputStream()).thenReturn(ByteArrayOutputStream())
        `when`(socket.close()).then {
            `when`(socket.isClosed).thenReturn(true)
        }
        return socket
    }

    fun output(): ByteArrayOutputStream {
       return socket.getOutputStream() as ByteArrayOutputStream
    }

    fun setInput(str: String, charset: Charset = Charsets.UTF_8) {
        val stream = str.byteInputStream(charset)
        `when`(socket.getInputStream()).thenReturn(stream)
    }

    fun setJsonInput(header: String, obj: Any) {
        setInput(header + JsonMapper.writeValueAsString(obj))
    }

    fun accept() {
        acceptQueue.put(socket)
        await().until { socket.isClosed }
    }

    fun assertOutput(exp: String) {
        assertArrayEquals(exp.toByteArray(), output().toByteArray())
    }

    fun assertJsonOutput(exp: Any) {
        assertOutput(JsonMapper.writeValueAsString(exp))
    }

    override fun close() {
        acceptQueue.put(newSocket())
    }

}