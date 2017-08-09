package com.gitlab.daring.fms.zabbix.api

import com.gitlab.daring.fms.common.config.ConfigUtils.configFromString
import com.gitlab.daring.fms.test.CommonTestUtils.assertError
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec
import java.time.Duration

class ZabbixApiHelperTest : FunSpec({

    test("init") {
        val c = configFromString("{" +
                "host=h1, user=u1, password=p1, tokenTimeout=1s, " +
                "connectTimeout=2s, readTimeout=3s, writeTimeout=4s, " +
                "retryOnConnectionFailure=true" +
                "}"
        )
        val h = ZabbixApiHelper(c)
        h.host shouldBe "h1"
        h.url shouldBe "http://h1/zabbix/api_jsonrpc.php"
        h.authParams shouldBe AuthParams("u1", "p1", Duration.ofSeconds(1))
        val hc = h.httpClient
        hc.connectTimeoutMillis() shouldBe 2000
        hc.readTimeoutMillis() shouldBe 3000
        hc.writeTimeoutMillis() shouldBe 4000
        hc.retryOnConnectionFailure() shouldBe true
    }

    test("call") {
        val mhc = MockHttpClient()
        mhc.expUrl = "http://h1/zabbix/api_jsonrpc.php"
        mhc.addResponse(200, "{result: 'a1'}")
        mhc.addResponse(200, "{result: 'r1'}")
        val h = ZabbixApiHelper("h1", AuthParams("u1", "p1"), mhc.client)
        val r1 = h.call("m1", mapOf("p1" to "v1"))
        mhc.assertRequest("user.login", null, "user" to "u1", "password" to "p1")
        mhc.assertRequest("m1", "a1", "p1" to "v1")
        r1.textValue() shouldBe "r1"
    }

    test("call error") {
        val mhc = MockHttpClient()
        val h = ZabbixApiHelper("h1", AuthParams("u1", "p1"), mhc.client)
        mhc.addResponse(401, "")
        assertError("code=401") { h.call("m1", mapOf()) }
        mhc.addResponse(200, "{error: 'err1'}")
        assertError("error=\"err1\"") { h.call("m1", mapOf()) }
    }

})