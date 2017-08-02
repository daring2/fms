package com.gitlab.daring.fms.zabbix.agent.active

//TODO merge with ItemValue
data class AgentItemValue(
        val host: String,
        val key: String,
        val value: String,
        val clock: Long,
        val ns: Long,
        val lastlogsize: Long,
        val mtime: Long
)