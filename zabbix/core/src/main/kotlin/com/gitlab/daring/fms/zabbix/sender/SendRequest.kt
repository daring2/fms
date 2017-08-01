package com.gitlab.daring.fms.zabbix.sender

import com.fasterxml.jackson.databind.node.ObjectNode
import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import com.gitlab.daring.fms.zabbix.model.ItemValue
import com.gitlab.daring.fms.zabbix.sender.ZabbixSenderUtils.addTimeFields
import java.time.Duration

data class SendRequest(
        val data: List<ItemValue>,
        val time: Duration? = null,
        val proxy: String? = null
) {

    fun buildJson(): ObjectNode {
        val n = JsonMapper.createObjectNode()
        if (proxy != null) {
            n.put("request", "history data")
            n.put("host", proxy)
        } else {
            n.put("request", "sender data")
        }
        val dn = n.putArray("data")
        data.forEachIndexed { i, v ->
            dn.add(v.buildJson(i))
        }
        time?.let { addTimeFields(n, it, 0) }
        return n
    }

}