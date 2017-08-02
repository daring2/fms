package com.gitlab.daring.fms.zabbix.agent.active

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class AgentRequest(
        val request: String,
        val host: String?,
        val data: List<AgentItemValue>?
)