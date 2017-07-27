package com.gitlab.daring.fms.zabbix.sender

import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import com.gitlab.daring.fms.common.network.SocketProvider
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.*
import java.io.ByteArrayOutputStream
import java.net.Socket

class ZabbixSenderTest {

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
        val str = "0".repeat(13) + JsonMapper.writeValueAsString(r)
        `when`(s.getInputStream()).thenReturn(str.byteInputStream())
    }

}