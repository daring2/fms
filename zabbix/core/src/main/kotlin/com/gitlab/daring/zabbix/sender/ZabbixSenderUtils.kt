package com.gitlab.daring.zabbix.sender

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.google.common.base.CharMatcher
import java.time.Duration

internal object ZabbixSenderUtils {

    val ValueCharMatcher = CharMatcher.javaIsoControl().and(CharMatcher.whitespace().negate())
    val HeaderSize = 13

    /**
     * Removes unsupported chars from value string
     */
    fun normalizeValue(v: String): String {
        return ValueCharMatcher.removeFrom(v)
    }

    /**
     * Adds time fields (clock, ns) to given [JsonNode]
     */
    fun addTimeFields(n: ObjectNode, time: Duration, nanos: Int): Unit {
        n.put("clock", time.seconds)
        n.put("ns", time.nano + nanos)
    }

}