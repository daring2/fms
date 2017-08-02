package com.gitlab.daring.fms.zabbix.util

import org.junit.Assert.*
import org.junit.Test
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.HeaderSize
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.parseJsonResponse
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.parseResponse
import java.io.ByteArrayInputStream
import kotlin.text.Charsets.UTF_8

class ZabbixProtocolUtilsTest {

    @Test
    fun testParseResponse() {
        val bs1 = ("0".repeat(HeaderSize) + "v1").toByteArray(UTF_8)
        assertEquals("v1", parseResponse(bs1, UTF_8))
    }

    @Test
    fun testParseJsonResponse() {
        val bs1 = ("0".repeat(HeaderSize) + "{v: 'v1'}").toByteArray(UTF_8)
        val r1 = parseJsonResponse<TestBean>(ByteArrayInputStream(bs1))
        assertEquals(TestBean("v1"), r1)
    }

}

data class TestBean(val v: String)