package com.gitlab.daring.fms.zabbix.agent.active

import com.gitlab.daring.fms.common.config.ConfigUtils.configFromString
import com.gitlab.daring.fms.zabbix.util.MockSocketProvider
import com.gitlab.daring.fms.zabbix.util.ZabbixTestUtils.TestHeader
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec
import org.awaitility.Awaitility.await
import org.mockito.Mockito.verify
import java.util.concurrent.ThreadPoolExecutor

class AgentActiveClientTest : FunSpec({

    test("init") {
        val c = configFromString("{ port=1, readTimeout=2s, executor { size=3, maxSize=4 }}")
        val cl = AgentActiveClient(c)
        cl.port shouldBe 1
        cl.readTimeout shouldBe 2000
        val exec = cl.executor as ThreadPoolExecutor
        exec.corePoolSize shouldBe 3
        exec.maximumPoolSize shouldBe 4
    }

    test("start/stop") {
        val cl = AgentActiveClient()
        cl.isStarted shouldBe false
        cl.serverSocket shouldBe null

        cl.start()
        cl.isStarted shouldBe true
        await().until { cl.serverSocket != null }
        val serverSocket = cl.serverSocket
        serverSocket?.isClosed shouldBe false

        cl.stop()
        cl.isStarted shouldBe false
        await().until { cl.serverSocket == null }
        serverSocket?.isClosed shouldBe true
    }

    test("active checks - invalid request") {
        val sp = MockSocketProvider()
        val cl = AgentActiveClient(socketProvider = sp.serverProvider)
        cl.start()
        sp.setInput("$TestHeader{request:'none'}")
        sp.accept()
        sp.assertJsonOutput(AgentResponse("failed", "invalid request"))
        verify(sp.socket).close()
        sp.close()
    }

    // sp.setInput("{request:'active checks',host:'h1'}")

    //TODO implement

})