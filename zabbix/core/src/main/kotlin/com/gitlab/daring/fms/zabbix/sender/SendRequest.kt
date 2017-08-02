package com.gitlab.daring.fms.zabbix.sender

import com.fasterxml.jackson.databind.node.ObjectNode
import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import com.gitlab.daring.fms.zabbix.model.ItemValue
import com.gitlab.daring.fms.zabbix.sender.ZabbixSenderUtils.addTimeFields
import com.gitlab.daring.fms.zabbix.sender.ZabbixSenderUtils.normalizeValue
import java.time.Instant

data class SendRequest(
        val data: List<ItemValue>,
        val time: Instant? = null,
        val proxy: String? = null
) {

    internal fun buildJson(): ObjectNode {
        val n = JsonMapper.createObjectNode()
        if (proxy != null) {
            n.put("request", "history data")
            n.put("host", proxy)
        } else {
            n.put("request", "sender data")
        }
        val dn = n.putArray("data")
        data.forEachIndexed { i, v ->
            dn.add(buildValueJson(v, i))
        }
        time?.let { addTimeFields(n, it, 0) }
        return n
    }

    internal fun buildValueJson(v: ItemValue, index: Int): ObjectNode {
        val n = JsonMapper.valueToTree<ObjectNode>(v)
        n.put("value", normalizeValue(v.value))
        if (v.ns != null) n.put("ns", v.ns + index)
        return n
    }

}