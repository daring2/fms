package com.gitlab.daring.fms.zabbix.util

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec
import org.awaitility.Awaitility.await
import org.mockito.Mockito
import java.net.Socket
import java.util.concurrent.Executors.newFixedThreadPool

class ZabbixSocketServerTest : FunSpec({

    test("start/stop") {
        TestSockerServer().use {
            it.isStarted shouldBe false
            it.serverSocket shouldBe null

            it.start()
            it.isStarted shouldBe true
            await().until { it.serverSocket != null }
            val serverSocket = it.serverSocket
            Mockito.verify(it.sp.serverProvider).createServerSocket(10)
            serverSocket?.isClosed shouldBe false

            it.stop()
            it.isStarted shouldBe false
            await().until { it.serverSocket == null }
            serverSocket?.isClosed shouldBe true
        }
    }

})

class TestSockerServer : ZabbixSocketServer() {

    val sp = MockSocketProvider()
    val executor = newFixedThreadPool(2)

    override fun executor() = executor

    override fun createServerSocket() = sp.serverProvider.createServerSocket(10)

    override fun process(socket: Socket) {}

}