package com.gitlab.daring.fms.zabbix.agent.active

import com.gitlab.daring.fms.common.config.ConfigUtils.configFromString
import com.gitlab.daring.fms.zabbix.model.Item
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

    fun checkProcess(req: AgentRequest, res: AgentResponse, f: (AgentActiveClient) -> Unit = {}) {
        MockSocketProvider().use { sp ->
            AgentActiveClient(socketProvider = sp.serverProvider).use { cl ->
                f(cl)
                cl.start()
                sp.setJsonInput(TestHeader, req)
                sp.accept()
                sp.assertJsonOutput(res)
                verify(sp.socket).close()
            }
        }
    }

    test("invalid request") {
        checkProcess(
                AgentRequest("none"),
                AgentResponse("failed", "invalid request")
        )
    }

    test("active checks") {
        checkProcess(
                AgentRequest("active checks", "h1"),
                AgentResponse("failed", "host h1 not found")
        )
        val items1 = listOf(Item("i11"), Item("i12"))
        val items2 = listOf(Item("i21"), Item("i22"))
        checkProcess(
                AgentRequest("active checks", "h1"),
                AgentResponse("success", data = items1)
        ) { cl ->
            cl.setItems("h1", items1)
            cl.setItems("h2", items2)
        }
    }

    //TODO implement

})