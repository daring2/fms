package com.gitlab.daring.fms.zabbix.agent.passive

import com.gitlab.daring.fms.common.config.ConfigUtils.configFromString
import com.gitlab.daring.fms.common.network.SocketProviderImpl
import com.gitlab.daring.fms.test.CommonTestUtils.assertError
import com.gitlab.daring.fms.zabbix.model.Item
import com.gitlab.daring.fms.zabbix.model.ItemValue
import com.gitlab.daring.fms.zabbix.util.MockSocketProvider
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.ZbxError
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.ZbxNotSupported
import com.gitlab.daring.fms.zabbix.util.ZabbixTestUtils.TestHeader
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec
import org.mockito.Mockito.verify

class AgentPassiveClientTest : FunSpec({

    test("init") {
        val c = configFromString("{host=h1, port=10, connectTimeout=1s, readTimeout=2s}")
        val sender = AgentPassiveClient(c)
        sender.host shouldBe "h1"
        sender.port shouldBe 10
        val sp = sender.socketProvider as SocketProviderImpl
        sp.connectTimeout shouldBe 1000
        sp.readTimeout shouldBe 2000
    }

    fun testRequest(item: String, result: String, f: (AgentPassiveClient) -> Unit) {
        val sp = MockSocketProvider()
        val cl = AgentPassiveClient("h1", 10, sp.provider)
        sp.setInput(TestHeader + result)
        f(cl)
        verify(sp.provider).createSocket("h1", 10)
        sp.assertOutput("$item\n")
        verify(sp.socket).close()
    }

    test("request") {
        val iv1 = ItemValue("", "i1", "v1")
        testRequest("i1", iv1.value) { cl ->
            cl.request(Item("i1")) shouldBe iv1
        }
    }

    test("not supported items") {
        val iv1 = ItemValue("", "i1", "err1", state = 1)
        testRequest("i1", ZbxNotSupported + "err1") { cl ->
            cl.request(Item("i1")) shouldBe iv1
        }
    }

    test("request error") {
        testRequest("i1", ZbxError) { cl ->
            assertError(ZbxError) { cl.request(Item("i1")) }
        }
    }

})