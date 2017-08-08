package com.gitlab.daring.fms.test

import org.junit.Assert

object CommonTestUtils {

    fun assertError(error: String, func: () -> Unit) {
        try {
            func(); Assert.fail()
        } catch (e: Exception) {
            Assert.assertEquals(error, e.message)
        }
    }

}