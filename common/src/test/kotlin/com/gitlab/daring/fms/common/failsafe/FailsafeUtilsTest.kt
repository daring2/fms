package com.gitlab.daring.fms.common.failsafe

import com.gitlab.daring.fms.common.config.ConfigUtils.configFromString
import com.gitlab.daring.fms.common.failsafe.FailsafeUtils.newCircuitBreaker
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec

class FailsafeUtilsTest : FunSpec({

    test("newCircuitBreaker") {
        val c = configFromString("{ threshold = 10, delay = 3s }")
        val cb = newCircuitBreaker(c)
        cb.failureThreshold.ratio shouldBe 1.0
        cb.delay.toMillis() shouldBe 3000L
    }

})