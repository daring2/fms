package com.gitlab.daring.fms.common.util

import com.gitlab.daring.fms.test.CommonTestUtils.assertError
import io.kotlintest.mock.mock
import io.kotlintest.specs.FunSpec
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

class CommonExtensionTest : FunSpec({

    test("closeQuietly") {
        val c1 = mock<AutoCloseable>()
        c1.closeQuietly()
        verify(c1).close()
        val c2 = mock<AutoCloseable>()
        `when`(c2.close()).thenThrow(RuntimeException("err1"))
        assertError("err1") { c2.close() }
    }

})