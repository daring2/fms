package com.gitlab.daring.fms.common.util

import com.gitlab.daring.fms.common.util.CommonUtils.parseDuration
import io.kotlintest.specs.FunSpec
import org.junit.Assert.assertEquals
import java.time.Duration

class CommonUtilsTest : FunSpec({

    test("parseDuration") {
        assertEquals(Duration.ofMillis(1), parseDuration("1"))
        assertEquals(Duration.ofMillis(1000), parseDuration("1s"))
        assertEquals(Duration.ofMillis(60000), parseDuration("1m"))
    }

})