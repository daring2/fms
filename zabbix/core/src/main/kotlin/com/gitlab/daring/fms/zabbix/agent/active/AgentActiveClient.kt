package com.gitlab.daring.fms.zabbix.agent.active

import com.gitlab.daring.fms.common.config.getHostAndPort
import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import com.gitlab.daring.fms.common.network.SocketProvider
import com.gitlab.daring.fms.common.network.SocketProviderImpl
import com.gitlab.daring.fms.zabbix.agent.active.AgentRequest.Companion.ActiveChecks
import com.gitlab.daring.fms.zabbix.agent.active.AgentRequest.Companion.AgentData
import com.gitlab.daring.fms.zabbix.model.ItemValue
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.parseJsonResponse
import com.typesafe.config.Config

class AgentActiveClient(
        val serverHost: String,
        val serverPort: Int = 10051,
        val socketProvider: SocketProvider = SocketProviderImpl(3000, 10000)
) {

    fun queryItems(host: String): AgentResponse {
        return sendRequest(AgentRequest(ActiveChecks, host))
    }

    fun sendValues(values: List<ItemValue>): AgentResponse {
        return sendRequest(AgentRequest(AgentData, data = values))
    }

    fun sendRequest(req: AgentRequest): AgentResponse {
        socketProvider.createSocket(serverHost, serverPort).use {
            JsonMapper.writeValue(it.getOutputStream(), req)
            return parseJsonResponse(it.getInputStream())
        }
    }

    companion object {

        fun create(c: Config): AgentActiveClient {
            val hp = c.getHostAndPort("server").withDefaultPort(10051)
            return AgentActiveClient(hp.host, hp.port, SocketProviderImpl(c))
        }

    }

}