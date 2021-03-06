package com.gitlab.daring.fms.zabbix.agent.active

import com.gitlab.daring.fms.common.config.ConfigUtils.configFromString
import com.gitlab.daring.fms.common.util.closeQuietly
import com.gitlab.daring.fms.zabbix.agent.active.AgentRequest.Companion.AgentData
import com.gitlab.daring.fms.zabbix.agent.active.AgentResponse.Companion.Failed
import com.gitlab.daring.fms.zabbix.agent.active.AgentResponse.Companion.Success
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
import org.mockito.Mockito.verify
import java.util.concurrent.ThreadPoolExecutor

class AgentActiveServerTest : FunSpec() {

    init {

        test("constructors") {
            val c = configFromString("{ port=1, readTimeout=2s,  executor { size=3, maxSize=4 }}")
            val srv = AgentActiveServer(c)
            srv.port shouldBe 1
            srv.readTimeout shouldBe 2000
            val exec = srv.executor as ThreadPoolExecutor
            exec.corePoolSize shouldBe 3
            exec.maximumPoolSize shouldBe 4
        }

        testWithContext("invalid request") {
            srv.start()
            checkProcess(AgentRequest("none"), AgentResponse(Failed, "invalid request"))
        }

        testWithContext("active checks") {
            val regexps1 = listOf(CheckRegexp("rn1", "exp1"))
            srv.regexps = regexps1
            srv.start()
            val table = table(
                    headers("host", "response"),
                    row("h0", AgentResponse(Failed, "host h0 not found")),
                    row("h1", AgentResponse(Success, data = items1, regexp = regexps1)),
                    row("h2", AgentResponse(Success, data = items2, regexp = regexps1))
            )
            forAll(table) { h, r ->
                checkProcess(AgentRequest("active checks", h), r)
            }
        }

        testWithContext("agent data") {
            values shouldBe emptyList<ItemValue>()
            srv.start()
            val req1 = AgentRequest(AgentData, data = listOf(
                    ItemValue("h1", "i11", "v1", lastlogsize = 1, mtime = 2),
                    ItemValue("h1", "i12", "v2")
            ))
            checkProcess(req1, AgentResponse(Success, ""))
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
        val srv = AgentActiveServer(10, 100)
        val items1 = listOf(Item("i11"), Item("i12"))
        val items2 = listOf(Item("i21"), Item("i22"))
        val values = ArrayList<ItemValue>()

        init {
            srv.socketProvider = sp.serverProvider
            srv.setItems("h1", items1)
            srv.setItems("h2", items2)
            srv.valueListener = { vs -> values.addAll(vs) }
        }

        fun checkProcess(req: AgentRequest, res: AgentResponse) {
            sp.newSocket()
            sp.setJsonInput(TestHeader, req)
            sp.accept()
            sp.assertJsonOutput(res)
            verify(sp.socket).close()
        }

        override fun close() {
            srv.closeQuietly()
            sp.closeQuietly()
        }
    }

}