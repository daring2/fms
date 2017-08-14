package com.gitlab.daring.fms.zabbix.agent.active

import com.fasterxml.jackson.annotation.JsonInclude
import com.gitlab.daring.fms.zabbix.model.Item

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AgentResponse(
        val response: String,
        val info: String? = null,
        val data: Collection<Item>? = null,
        val regexp: Collection<CheckRegexp>? = null
) {

    val success get() = response == Success

    companion object {
        val Success = "success"
        val Failed = "failed"
    }

}