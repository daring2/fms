package com.gitlab.daring.fms.zabbix.api

import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper

data class ZabbixApiRequest(
        val method: String,
        val params: Map<String, Any> = HashMap(),
        val jsonrpc: String = "2.0",
        val auth: String? = null,
        val id: Int = 1
) {

    fun toBytes(): ByteArray {
        return JsonMapper.writeValueAsBytes(this)
    }

}