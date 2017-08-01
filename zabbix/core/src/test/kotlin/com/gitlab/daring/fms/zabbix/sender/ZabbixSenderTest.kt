package com.gitlab.daring.fms.zabbix.sender

import com.gitlab.daring.fms.common.config.ConfigUtils.configFromString
import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import com.gitlab.daring.fms.common.network.SocketProvider
import com.gitlab.daring.fms.common.network.SocketProviderImpl
import com.gitlab.daring.fms.zabbix.model.ItemValue
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.HeaderSize
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.*
import java.io.ByteArrayOutputStream
import java.net.Socket

class ZabbixSenderTest {

    @Test
    fun testInit() {
        val c = configFromString("{host=h1, port=10, connectTimeout=1s, readTimeout=2s}")
        val sender = ZabbixSender(c)
        assertEquals("h1", sender.host)
        assertEquals(10, sender.port)
        val sp = sender.socketProvider as SocketProviderImpl
        assertEquals(1000, sp.connectTimeout)
        assertEquals(2000, sp.readTimeout)
    }

    @Test
    fun testSend() {
        val sp = mock(SocketProvider::class.java)
        val s = mock(Socket::class.java)
        `when`(sp.createSocket(any(), anyInt())).thenReturn(s)
        val out = mockOutputStream(s)
        val sr = SendResult("r1", "info1")
        mockInputStream(s, sr)
        val sender = ZabbixSender("h1", 10, sp)

        val v1 = ItemValue("h1", "k1", "v1")
        val req1 = SendRequest(listOf(v1))
        assertEquals(sr, sender.send(req1))
        verify(sp).createSocket("h1", 10)
        val expOut = JsonMapper.writeValueAsBytes(req1.buildJson())
        assertArrayEquals(expOut, out.toByteArray())
        verify(s).close()
    }

    fun mockOutputStream(s: Socket): ByteArrayOutputStream {
        val out = ByteArrayOutputStream()
        `when`(s.getOutputStream()).thenReturn(out)
        return out
    }

    fun mockInputStream(s: Socket, r: SendResult) {
        val str = "0".repeat(HeaderSize) + JsonMapper.writeValueAsString(r)
        `when`(s.getInputStream()).thenReturn(str.byteInputStream())
    }

}