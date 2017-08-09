package com.gitlab.daring.fms.common.failsafe

import com.gitlab.daring.fms.common.config.ConfigUtils.configFromString
import com.gitlab.daring.fms.common.failsafe.FailsafeUtils.newCircuitBreaker
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec
import net.jodah.failsafe.util.Ratio

class FailsafeUtilsTest : FunSpec({

    fun assertRatio(n: Int, r: Ratio) {
        r.numerator shouldBe n
        r.denominator shouldBe n
    }

    test("newCircuitBreaker") {
        val cb1 = newCircuitBreaker(configFromString(
                "{ threshold = 11, delay = 3s }"
        ))
        assertRatio(11, cb1.failureThreshold)
        assertRatio(1, cb1.successThreshold)
        cb1.delay.toMillis() shouldBe 3000L

        val cb2 = newCircuitBreaker(configFromString(
                "{ threshold = 11, successThreshold = 12, delay = 3s }"
        ))
        assertRatio(11, cb2.failureThreshold)
        assertRatio(12, cb2.successThreshold)
        cb2.delay.toMillis() shouldBe 3000L
    }

})