package com.gitlab.daring.fms.zabbix.sender

import com.gitlab.daring.fms.common.config.ConfigUtils.configFromString
import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import com.gitlab.daring.fms.common.network.SocketProviderImpl
import com.gitlab.daring.fms.zabbix.model.ItemValue
import com.gitlab.daring.fms.zabbix.util.MockSocketProvider
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.HeaderSize
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.verify

class ZabbixSenderTest {

    @Test
    fun testInit() {
        val c = configFromString("{ host=h1, port=10, connectTimeout=1s, readTimeout=2s }")
        val sender = ZabbixSender(c)
        assertEquals("h1", sender.host)
        assertEquals(10, sender.port)
        val sp = sender.socketProvider as SocketProviderImpl
        assertEquals(1000, sp.connectTimeout)
        assertEquals(2000, sp.readTimeout)
    }

    @Test
    fun testSend() {
        val sp = MockSocketProvider()
        val sr = SendResult("r1", "info1")
        mockInputStream(sp, sr)
        val sender = ZabbixSender("h1", 10, sp)

        val v1 = ItemValue("h1", "k1", "v1")
        val req1 = SendRequest(listOf(v1))
        assertEquals(sr, sender.send(req1))
        verify(sp.provider).createSocket("h1", 10)
        val expOut = JsonMapper.writeValueAsBytes(req1.buildJson())
        assertArrayEquals(expOut, sp.output.toByteArray())
        verify(sp.socket).close()
    }

    fun mockInputStream(sp: MockSocketProvider, r: SendResult) {
        val str = "0".repeat(HeaderSize) + JsonMapper.writeValueAsString(r)
        sp.setInput(str)
    }

}