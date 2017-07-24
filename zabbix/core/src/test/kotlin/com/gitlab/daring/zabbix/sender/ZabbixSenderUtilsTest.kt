package com.gitlab.daring.zabbix.sender

import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import com.gitlab.daring.zabbix.sender.ZabbixSenderUtils.ValueCharMatcher
import com.gitlab.daring.zabbix.sender.ZabbixSenderUtils.addTimeFields
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration

class ZabbixSenderUtilsTest {

    @Test
    fun testNormalizeValue() {
        assertEquals("v1234", ValueCharMatcher.removeFrom("v1234"))
        assertEquals("v123\n 4", ValueCharMatcher.removeFrom("v1\u00002\u001f3\n 4"))
    }

    @Test
    fun testAddTimeFields() {
        checkAddTimeFields(0, 0, 0, 0)
        checkAddTimeFields(1000, 0, 1, 0)
        checkAddTimeFields(2003, 4, 2, 3000004)
    }

    fun checkAddTimeFields(millis: Long, nanos: Int, expClock: Long, expNanos: Int) {
        val n = JsonMapper.createObjectNode()
        addTimeFields(n, Duration.ofMillis(millis), nanos)
        assertEquals(2, n.size())
        assertEquals(expClock, n.get("clock").longValue())
        assertEquals(expNanos, n.get("ns").intValue())
    }

}