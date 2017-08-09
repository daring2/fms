package com.gitlab.daring.fms.zabbix.agent.active

import com.gitlab.daring.fms.common.config.ConfigUtils.configFromString
import com.gitlab.daring.fms.zabbix.model.Item
import com.gitlab.daring.fms.zabbix.model.ItemValue
import com.gitlab.daring.fms.zabbix.util.MockSocketProvider
import com.gitlab.daring.fms.zabbix.util.ZabbixTestUtils.TestHeader
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import io.kotlintest.properties.headers
import io.kotlintest.properties.row
import io.kotlintest.properties.table
import io.kotlintest.specs.FunSpec
import org.awaitility.Awaitility.await
import org.mockito.Mockito.verify
import java.util.concurrent.ThreadPoolExecutor

class AgentActiveClientTest : FunSpec() {

    init {

        test("init") {
            val c = configFromString("{ port=1, readTimeout=2s, executor { size=3, maxSize=4 }}")
            val cl = AgentActiveClient(c)
            cl.port shouldBe 1
            cl.readTimeout shouldBe 2000
            val exec = cl.executor as ThreadPoolExecutor
            exec.corePoolSize shouldBe 3
            exec.maximumPoolSize shouldBe 4
        }

        testWithContext("start/stop") {
            cl.isStarted shouldBe false
            cl.serverSocket shouldBe null

            cl.start()
            cl.isStarted shouldBe true
            await().until { cl.serverSocket != null }
            val serverSocket = cl.serverSocket
            verify(sp.serverProvider).createServerSocket(10)
            serverSocket?.isClosed shouldBe false

            cl.stop()
            cl.isStarted shouldBe false
            await().until { cl.serverSocket == null }
            serverSocket?.isClosed shouldBe true
        }

        testWithContext("invalid request") {
            cl.start()
            checkProcess(AgentRequest("none"), AgentResponse("failed", "invalid request"))
        }

        testWithContext("active checks") {
            cl.start()
            val table = table(
                    headers("host", "response"),
                    row("h0", AgentResponse("failed", "host h0 not found")),
                    row("h1", AgentResponse("success", data = items1)),
                    row("h2", AgentResponse("success", data = items2))
            )
            forAll(table) { h, r ->
                checkProcess(AgentRequest("active checks", h), r)
            }
        }

        testWithContext("agent data") {
            values shouldBe emptyList<ItemValue>()
            cl.start()
            val req1 = AgentRequest("agent data", data = listOf(
                    ItemValue("h1", "i11", "v1", lastlogsize = 1, mtime = 2),
                    ItemValue("h1", "i12", "v2")
            ))
            checkProcess(req1, AgentResponse("success", ""))
            values shouldBe req1.data
            items1[0] shouldBe Item("i11", lastlogsize = 1, mtime = 2)
            items1[1] shouldBe Item("i12")
        }
    }

    internal fun testWithContext(name: String, f: TestContext.() -> Unit) {
        test(name) { TestContext().use(f) }
    }

    internal class TestContext : AutoCloseable {

        val sp = MockSocketProvider()
        val cl = AgentActiveClient(10, 100, socketProvider = sp.serverProvider)
        val items1 = listOf(Item("i11"), Item("i12"))
        val items2 = listOf(Item("i21"), Item("i22"))
        val values = ArrayList<ItemValue>()

        init {
            cl.setItems("h1", items1)
            cl.setItems("h2", items2)
            cl.valueListener = { vs -> values.addAll(vs) }
        }

        fun checkProcess(req: AgentRequest, res: AgentResponse) {
            sp.newSocket()
            sp.setJsonInput(TestHeader, req)
            sp.accept()
            sp.assertJsonOutput(res)
            verify(sp.socket).soTimeout = 100
            verify(sp.socket).close()
        }

        override fun close() {
            cl.close()
            sp.close()
        }
    }

}