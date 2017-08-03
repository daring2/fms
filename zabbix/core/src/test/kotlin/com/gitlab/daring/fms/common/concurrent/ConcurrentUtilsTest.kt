package com.gitlab.daring.fms.common.concurrent

import com.gitlab.daring.fms.common.concurrent.ConcurrentUtils.newExecutor
import com.gitlab.daring.fms.common.config.ConfigUtils.configFromString
import org.junit.Assert.assertEquals
import org.junit.Test

class ConcurrentUtilsTest {

    @Test
    fun testNewExecutor() {
        val exec = newExecutor(configFromString("{size = 5}"))
        assertEquals(5, exec.corePoolSize)
        assertEquals(5, exec.maximumPoolSize)
    }

}