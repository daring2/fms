package com.gitlab.daring.fms.zabbix.util

import org.junit.Assert
import org.junit.Assert.assertEquals

object ZabbixTestUtils {

    fun assertError(error: String, func: () -> Unit) {
        try {
            func(); Assert.fail()
        } catch (e: Exception) {
            assertEquals(error, e.message)
        }
    }
    
}