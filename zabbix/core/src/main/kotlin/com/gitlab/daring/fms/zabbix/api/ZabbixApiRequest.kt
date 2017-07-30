package com.gitlab.daring.fms.zabbix.api

import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper

data class ZabbixApiRequest(
        val method: String,
        val params: Map<String, Any?> = HashMap(),
        val auth: String? = null,
        val jsonrpc: String = "2.0",
        val id: Int = 1
) {

    fun toJsonBytes(): ByteArray {
        return JsonMapper.writeValueAsBytes(this)
    }

    fun toJsonString(): String {
        return JsonMapper.writeValueAsString(this)
    }

}