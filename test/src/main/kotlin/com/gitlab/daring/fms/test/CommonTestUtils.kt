package com.gitlab.daring.fms.test

import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldThrowAny

object CommonTestUtils {

    fun assertError(error: String, func: () -> Unit): Throwable {
        val e = shouldThrowAny(func)
        e.message shouldBe error
        return e
    }

}