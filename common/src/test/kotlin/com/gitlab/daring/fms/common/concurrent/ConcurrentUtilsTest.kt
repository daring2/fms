package com.gitlab.daring.fms.common.concurrent

import com.gitlab.daring.fms.common.concurrent.ConcurrentUtils.newExecutor
import com.gitlab.daring.fms.common.config.ConfigUtils.configFromString
import io.kotlintest.specs.FunSpec
import org.junit.Assert.assertEquals
import java.util.concurrent.TimeUnit.MILLISECONDS

class ConcurrentUtilsTest : FunSpec({

    test("newExecutor") {
        val e1 = newExecutor(configFromString("{ size = 5 }"))
        assertEquals(5, e1.corePoolSize)
        assertEquals(5, e1.maximumPoolSize)
        assertEquals(60000, e1.getKeepAliveTime(MILLISECONDS))
        val e2 = newExecutor(configFromString("{ size = 1, maxSize=2, keepAlive=3s }"))
        assertEquals(1, e2.corePoolSize)
        assertEquals(2, e2.maximumPoolSize)
        assertEquals(3000, e2.getKeepAliveTime(MILLISECONDS))
    }

})