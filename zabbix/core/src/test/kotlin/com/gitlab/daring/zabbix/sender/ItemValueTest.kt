package com.gitlab.daring.zabbix.sender

import org.junit.Assert.*
import com.gitlab.daring.fms.common.json.*
import org.junit.Test
import java.time.Duration

class ItemValueTest {

    @Test
    fun testBuildJson() {
        val v1 = ItemValue("h1", "k1", "v1")
        val m1 = hashMapOf("host" to "h1", "key" to "k1", "value" to "v1")
        assertEquals(m1, v1.buildJson(4).toMap())

        val v2 = v1.copy(isError = true)
        val m2 = m1 + ("state" to 1)
        assertEquals(m2, v2.buildJson(4).toMap())

        val v3 = v1.copy(time = Duration.ofMillis(2003))
        val m3 = m1 + listOf("clock" to 2L, "ns" to 3000004)
        assertEquals(m3, v3.buildJson(4).toMap())
    }

}