package com.gitlab.daring.fms.zabbix.sender

import com.fasterxml.jackson.databind.JsonNode
import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration

class SendRequestTest {

    @Test
    fun testBuildJson() {
        val vs1 = listOf(
                ItemValue("h1", "k1", "v1"),
                ItemValue("h2", "k2", "v2", isError = true)
        )
        val req1 = SendRequest(vs1, Duration.ofMillis(1002))
        val d1 =  "data: [" +
                "{host: 'h1', key: 'k1', value: 'v1'}," +
                "{host: 'h2', key: 'k2', value: 'v2', state: 1}" +
                "]"
        val n1 = "{request: 'sender data', $d1, clock: 1, ns: 2000000}"
        assertJsonEquals(n1, req1.buildJson())

        val req2 = req1.copy(proxy = "p1")
        val n2 = "{request: 'history data', host: 'p1', $d1, clock: 1, ns: 2000000}"
        assertJsonEquals(n2, req2.buildJson())
    }

    fun assertJsonEquals(exp: String, n: JsonNode) {
        val expNode = JsonMapper.readTree(exp)
        assertEquals("" + expNode, "" + n)
    }

}