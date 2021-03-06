package com.gitlab.daring.fms.zabbix.sender

import com.fasterxml.jackson.databind.JsonNode
import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import com.gitlab.daring.fms.common.json.toMap
import com.gitlab.daring.fms.zabbix.model.ItemValue
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant.ofEpochMilli

class SendRequestTest {

    @Test
    fun testBuildJson() {
        val vs1 = listOf(
                ItemValue("h1", "k1", "v1"),
                ItemValue("h2", "k2", "").withError("v2")
        )
        val req1 = SendRequest(vs1, ofEpochMilli(1002))
        val d1 =  "data: [" +
                "{host: 'h1', key: 'k1', value: 'v1', state: 0}," +
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

    @Test
    fun testBuildValueJson() {
        val req = SendRequest(emptyList())
        val v1 = ItemValue("h1", "k1", "v1")
        val m1 = hashMapOf("host" to "h1", "key" to "k1", "value" to "v1", "state" to 0)
        assertEquals(m1, req.buildValueJson(v1, 4).toMap())

        val v2 = v1.withTime(ofEpochMilli(2003))
        val m2 = m1 + listOf("clock" to 2L, "ns" to 3000004)
        assertEquals(m2, req.buildValueJson(v2, 4).toMap())
    }

}