package com.gitlab.daring.fms.zabbix.agent.active

import com.fasterxml.jackson.annotation.JsonInclude
import com.gitlab.daring.fms.zabbix.model.ItemValue

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AgentRequest(
        val request: String,
        val host: String? = null,
        val data: List<ItemValue>? = null,
        val clock: Long? = null,
        val ns: Int? = null
) {

    companion object {
        val ActiveChecks = "active checks"
        val AgentData = "agent data"
    }

}