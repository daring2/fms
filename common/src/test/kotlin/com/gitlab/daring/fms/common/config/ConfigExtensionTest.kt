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

}