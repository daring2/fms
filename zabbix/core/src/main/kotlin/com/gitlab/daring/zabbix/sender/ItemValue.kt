package com.gitlab.daring.zabbix.sender

import com.fasterxml.jackson.databind.node.ObjectNode
import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import com.gitlab.daring.zabbix.sender.ZabbixSenderUtils.addTimeFields
import java.time.Duration
import com.gitlab.daring.zabbix.sender.ZabbixSenderUtils.normalizeValue

data class ItemValue(
        val host: String,
        val key: String,
        val value: String,
        val time: Duration? = null,
        val isError: Boolean = false
) {

    fun buildJson(i: Int) : ObjectNode {
        val n = JsonMapper.createObjectNode()
        n.put("host", host)
        n.put("key", key)
        n.put("value", normalizeValue(value))
        if (isError) n.put("state", 1)
        time?.let { addTimeFields(n, it, i) }
        return n
    }

}