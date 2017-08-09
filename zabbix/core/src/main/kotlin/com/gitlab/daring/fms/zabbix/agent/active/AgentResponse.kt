package com.gitlab.daring.fms.zabbix.agent.active

import com.fasterxml.jackson.annotation.JsonInclude
import com.gitlab.daring.fms.zabbix.model.Item

@JsonInclude(JsonInclude.Include.NON_NULL)
internal data class AgentResponse(
        val response: String,
        val info: String? = null,
        val data: Collection<Item>? = null
)