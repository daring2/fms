package com.gitlab.daring.fms.zabbix.api

import com.gitlab.daring.fms.common.config.ConfigUtils.configFromString
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
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
        val mhc = MockHttpClient()
        mhc.expUrl = "http://h1/zabbix/api_jsonrpc.php"
        mhc.addResponse(200, "{result: 'a1'}")
        mhc.addResponse(200, "{result: 'r1'}")
        val h = ZabbixApiHelper("h1", AuthParams("u1", "p1"), mhc.client)
        val r1 = h.call("m1", mapOf("p1" to "v1"))
        mhc.assertRequest("user.login", null, "user" to "u1", "password" to "p1")
        mhc.assertRequest("m1", "a1", "p1" to "v1")
        assertEquals("r1", r1.textValue())
    }

    @Test
    fun testCallError() {
        val mhc = MockHttpClient()
        val h = ZabbixApiHelper("h1", AuthParams("u1", "p1"), mhc.client)
        mhc.addResponse(401, "")
        assertError("code=401") { h.call("m1", mapOf()) }
        mhc.addResponse(200, "{error: 'err1'}")
        assertError("error=\"err1\"") { h.call("m1", mapOf()) }
    }

    fun assertError(error: String, func: () -> Unit) {
        try {
            func(); fail()
        } catch (e: Exception) {
            assertEquals(error, e.message)
        }
    }
}