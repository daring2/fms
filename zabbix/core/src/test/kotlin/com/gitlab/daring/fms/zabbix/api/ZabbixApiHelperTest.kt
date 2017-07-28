package com.gitlab.daring.fms.zabbix.api

import com.gitlab.daring.fms.common.config.ConfigUtils.configFromString
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration

class ZabbixApiHelperTest {

    @Test
    fun testInit() {
        val c = configFromString("{" +
                "host=h1, user=u1, password=p1, tokenTimeout=1s, " +
                "connectTimeout=2s, readTimeout=3s, writeTimeout=4s, " +
                "retryOnConnectionFailure=true" +
                "}"
        )
        val h = ZabbixApiHelper(c)
        assertEquals("h1", h.host)
        assertEquals("http://h1/zabbix/api_jsonrpc.php", h.url)
        assertEquals(AuthParams("u1", "p1", Duration.ofSeconds(1)), h.authParams)
        val hc = h.httpClient
        assertEquals(2000, hc.connectTimeoutMillis())
        assertEquals(3000, hc.readTimeoutMillis())
        assertEquals(4000, hc.writeTimeoutMillis())
        assertEquals(true, hc.retryOnConnectionFailure())
    }

    @Test
    fun testCall() {
        //TODO implement
    }

}