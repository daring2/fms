package com.gitlab.daring.fms.zabbix.agent.active

import com.gitlab.daring.fms.common.config.ConfigUtils.configFromString
import com.gitlab.daring.fms.common.network.SocketProviderImpl
import com.gitlab.daring.fms.zabbix.agent.active.AgentRequest.Companion.ActiveChecks
import com.gitlab.daring.fms.zabbix.agent.active.AgentResponse.Companion.Success
import com.gitlab.daring.fms.zabbix.model.Item
import com.gitlab.daring.fms.zabbix.model.ItemValue
import com.gitlab.daring.fms.zabbix.util.MockSocketProvider
import com.gitlab.daring.fms.zabbix.util.ZabbixTestUtils.TestHeader
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec
import org.mockito.Mockito.verify

class AgentActiveClientTest : FunSpec({

    test("create") {
        val c1 = configFromString("{server=\"h1:10\", connectTimeout=1s, readTimeout=2s}")
        val cl1 = AgentActiveClient.create(c1)
        cl1.serverHost shouldBe "h1"
        cl1.serverPort shouldBe 10
        val sp = cl1.socketProvider as SocketProviderImpl
        sp.connectTimeout shouldBe 1000
        sp.readTimeout shouldBe 2000

        val c2 = configFromString("{server=h1, connectTimeout=1s, readTimeout=2s}")
        val cl2 = AgentActiveClient.create(c2)
        cl2.serverHost shouldBe "h1"
        cl2.serverPort shouldBe 10051
    }

    fun testRequest(req: AgentRequest, res: AgentResponse, f: (AgentActiveClient) -> Unit) {
        val sp = MockSocketProvider()
        val cl = AgentActiveClient("h1", 10, socketProvider = sp.provider)
        sp.setJsonInput(TestHeader, res)
        f(cl)
        verify(sp.provider).createSocket("h1", 10)
        sp.assertJsonOutput(req)
        verify(sp.socket).close()
    }

    test("queryItems") {
        val req1 = AgentRequest(ActiveChecks, "n1")
        val res1 = AgentResponse(Success, data = listOf(Item("i11"), Item("i12")))
        testRequest(req1, res1) { cl ->
            cl.queryItems("n1") shouldBe res1
        }
    }

    test("sendValues") {
        val items1 = listOf(
                ItemValue("h1", "i11", "v1"),
                ItemValue("h1", "i12", "v2")
        )
        val req1 = AgentRequest(AgentRequest.AgentData, data = items1)
        val res1 = AgentResponse(Success, "r1")
        testRequest(req1, res1) { cl ->
            cl.sendValues(items1) shouldBe res1
        }
    }

})