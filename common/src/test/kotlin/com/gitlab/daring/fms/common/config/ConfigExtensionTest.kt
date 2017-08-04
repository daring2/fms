package com.gitlab.daring.fms.common.config

import com.gitlab.daring.fms.common.config.ConfigUtils.configFromString
import org.junit.Assert.assertEquals
import org.junit.Test

class ConfigExtensionTest {

    @Test
    fun testGetMillis() {
        assertEquals(10, configFromString("p=10").getMillis("p"))
        assertEquals(10000, configFromString("p=10s").getMillis("p"))
        assertEquals(600000, configFromString("p=10m").getMillis("p"))
    }

    @Test
    fun testToMap() {
        val c1 = configFromString("p1=v1,p2=2")
        assertEquals(hashMapOf("p1" to "v1", "p2" to 2), c1.toMap())
    }

    @Test
    fun testToBean() {
        val c1 = configFromString("b1 { p1=v1,p2=2 }")
        val b1 = TestBean("v1", 2)
        assertEquals(b1, c1.getConfig("b1").toBean<TestBean>())
        assertEquals(b1, c1.getBean<TestBean>("b1"))
    }

    @Test
    fun testGetOpt() {
        val c1 = configFromString("p1=10")
        assertEquals(10, c1.getOpt { getInt("p1") })
        assertEquals(null, c1.getOpt { getInt("p2") })
    }

}

data class TestBean(
        val p1: String,
        val p2: Int
)