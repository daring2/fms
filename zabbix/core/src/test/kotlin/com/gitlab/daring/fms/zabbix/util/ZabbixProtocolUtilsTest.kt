package com.gitlab.daring.fms.zabbix.util

import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.HeaderSize
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.buildMessage
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.parseJsonResponse
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.parseResponse
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec
import org.junit.Assert.assertArrayEquals
import java.io.ByteArrayInputStream
import kotlin.text.Charsets.UTF_8

class ZabbixProtocolUtilsTest : FunSpec({

    test("parseResponse") {
        val bs1 = ("0".repeat(HeaderSize) + "v1").toByteArray()
        parseResponse(bs1, UTF_8) shouldBe "v1"
    }

    test("parseJsonResponse") {
        val bs1 = ("0".repeat(HeaderSize) + "{v: 'v1'}").toByteArray()
        parseJsonResponse<TestBean>(ByteArrayInputStream(bs1)) shouldBe TestBean("v1")
    }

    test("buildMessage") {
        assertArrayEquals(byteArrayOf(
                0x5a, 0x42, 0x58, 0x44, 0x01,
                0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x31, 0x32, 0x33
        ),  buildMessage("123"))
    }

})

data class TestBean(val v: String)