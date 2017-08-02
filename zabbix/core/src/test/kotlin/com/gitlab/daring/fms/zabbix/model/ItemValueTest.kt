package com.gitlab.daring.fms.zabbix.model

import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

class ItemValueTest {

    @Test
    fun testWithError() {
        val i1 = ItemValue("h1", "i1", "v1")
        assertEquals(0, i1.state)
        assertEquals("v1", i1.value)
        assertEquals(false, i1.isError)

        val i2 = i1.withError("err1")
        assertEquals(1, i2.state)
        assertEquals("err1", i2.value)
        assertEquals(true, i2.isError)
    }

    @Test
    fun testWithTime() {
        val i1 = ItemValue("h1", "i1", "v1").withTime(null)
        assertEquals(null, i1.clock)
        assertEquals(null, i1.ns)

        val i2 = i1.withTime(Instant.ofEpochMilli(2003))
        assertEquals(2L, i2.clock)
        assertEquals(3000000, i2.ns)
    }

}