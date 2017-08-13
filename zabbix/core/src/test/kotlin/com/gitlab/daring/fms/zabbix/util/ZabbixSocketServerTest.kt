package com.gitlab.daring.fms.zabbix.util

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec
import org.awaitility.Awaitility.await
import org.mockito.Mockito.verify
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors.newFixedThreadPool

class ZabbixSocketServerTest : FunSpec({

    fun testServer(name: String, f: TestSockerServer.() -> Unit) {
        test(name) { TestSockerServer().use(f) }
    }

    testServer("start/stop") {
        isStarted shouldBe false
        serverSocket shouldBe null

        start()
        isStarted shouldBe true
        await().until { serverSocket != null }
        val s1 = serverSocket
        verify(sp.serverProvider).createServerSocket(10)
        s1?.isClosed shouldBe false

        stop()
        isStarted shouldBe false
        await().until { serverSocket == null }
        s1?.isClosed shouldBe true
    }

    testServer("process") {
        start()
        sp.accept()
        processSocket shouldBe sp.socket
        verify(sp.socket).close()
    }

})

class TestSockerServer : ZabbixSocketServer() {

    val sp = MockSocketProvider()
    val executor = newFixedThreadPool(2)
    @Volatile
    var processSocket: Socket? = null

    override fun executor() = executor

    override fun createServerSocket(): ServerSocket {
        return sp.serverProvider.createServerSocket(10)
    }

    override fun process(socket: Socket) {
        processSocket = socket
    }

    override fun close() {
        super.close()
        sp.close()
    }

}