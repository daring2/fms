package com.gitlab.daring.fms.common.http

import com.gitlab.daring.fms.common.config.ConfigUtils.configFromString
import com.gitlab.daring.fms.common.http.HttpUtils.newHttpClient
import org.junit.Assert.assertEquals
import org.junit.Test

class HttpUtilsTest {

    @Test
    fun testNewHttpClient() {
        val c1 = configFromString("" +
                "connectTimeout=1s," +
                "readTimeout=2s," +
                "writeTimeout=3s," +
                "retryOnConnectionFailure=true"
        )
        val hc1 = newHttpClient(c1)
        assertEquals(1000, hc1.connectTimeoutMillis())
        assertEquals(2000, hc1.readTimeoutMillis())
        assertEquals(3000, hc1.writeTimeoutMillis())
        assertEquals(true, hc1.retryOnConnectionFailure())
    }

}