package com.gitlab.daring.fms.zabbix.api

import org.junit.Assert.assertEquals
import org.junit.Test

class AuthCacheTest {

    @Test
    fun testGetToken() {
        var ct = 1L
        val ac = AuthCache(10, { "t$ct" }, { ct })
        assertEquals("t1", ac.getToken())
        ct = 11
        assertEquals("t1", ac.getToken())
        ct = 12
        assertEquals("t12", ac.getToken())

        ac.accessTime = 20
        ct = 30
        assertEquals("t12", ac.getToken())
        ct = 31
        assertEquals("t31", ac.getToken())
    }

}