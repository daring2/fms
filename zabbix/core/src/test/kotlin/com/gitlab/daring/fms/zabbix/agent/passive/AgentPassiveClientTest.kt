package com.gitlab.daring.fms.zabbix.agent.passive

import com.gitlab.daring.fms.common.config.ConfigUtils
import com.gitlab.daring.fms.common.network.SocketProviderImpl
import com.gitlab.daring.fms.zabbix.model.Item
import com.gitlab.daring.fms.zabbix.model.ItemValue
import com.gitlab.daring.fms.zabbix.util.MockSocketProvider
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.HeaderSize
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.ZbxError
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.ZbxNotSupported
import com.gitlab.daring.fms.zabbix.util.ZabbixTestUtils.assertError
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.verify

class AgentPassiveClientTest {

    @Test
    fun testInit() {
        val c = ConfigUtils.configFromString("{host=h1, port=10, connectTimeout=1s, readTimeout=2s}")
        val sender = AgentPassiveClient(c)
        assertEquals("h1", sender.host)
        assertEquals(10, sender.port)
        val sp = sender.socketProvider as SocketProviderImpl
        assertEquals(1000, sp.connectTimeout)
        assertEquals(2000, sp.readTimeout)
    }

    @Test
    fun testRequest() {
        val iv1 = ItemValue("", "i1", "v1")
        testRequest("i1", iv1.value) { cl ->
            assertEquals(iv1, cl.request(Item("i1")))
        }
    }

    fun testRequest(item: String, result: String, f: (AgentPassiveClient) -> Unit) {
        val sp = MockSocketProvider()
        val cl = AgentPassiveClient("h1", 10, sp.provider)
        mockInputStream(sp, result)
        f(cl)
        verify(sp.provider).createSocket("h1", 10)
        sp.assertOutput("$item\n")
        verify(sp.socket).close()
    }

    @Test
    fun testNotSupportedItem() {
        val iv1 = ItemValue("", "i1", "err1", state = 1)
        testRequest("i1", ZbxNotSupported + "err1") { cl ->
            assertEquals(iv1, cl.request(Item("i1")))
        }
    }

    @Test
    fun testRequestError() {
        testRequest("i1", ZbxError) { cl ->
            assertError(ZbxError) { cl.request(Item("i1")) }
        }
    }

    fun mockInputStream(sp: MockSocketProvider, result: String) {
        sp.setInput("0".repeat(HeaderSize) + result)
    }

}