package com.gitlab.daring.fms.common.util

import com.gitlab.daring.fms.common.util.CommonUtils.parseDuration
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration

class CommonUtilsTest {

    @Test
    fun testParseDuration() {
        assertEquals(Duration.ofMillis(1), parseDuration("1"))
        assertEquals(Duration.ofMillis(1000), parseDuration("1s"))
        assertEquals(Duration.ofMillis(60000), parseDuration("1m"))
    }

}