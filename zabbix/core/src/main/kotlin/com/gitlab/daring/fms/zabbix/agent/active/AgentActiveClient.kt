package com.gitlab.daring.fms.zabbix.agent.active

import com.gitlab.daring.fms.common.concurrent.ConcurrentUtils.newExecutor
import com.gitlab.daring.fms.common.config.getMillis
import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import com.gitlab.daring.fms.zabbix.model.Item
import com.gitlab.daring.fms.zabbix.model.ItemValue
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.parseJsonResponse
import com.gitlab.daring.fms.zabbix.util.ZabbixSocketServer
import com.typesafe.config.Config
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors.newFixedThreadPool

class AgentActiveClient(
        override val port: Int = 10051,
        override val readTimeout: Int = 3000,
        override val executor: ExecutorService = newFixedThreadPool(2)
) : ZabbixSocketServer() {

    private val hostItems = ConcurrentHashMap<String, Map<String, Item>>()

    /**
     * Global regular expressions
     */
    @Volatile
    var regexps: List<CheckRegexp>? = null

    /**
     * Listener of new item values received from Zabbix agent
     */
    @Volatile
    var valueListener: (List<ItemValue>) -> Unit = {}

    constructor(c: Config) : this(
            c.getInt("port"),
            c.getMillis("readTimeout").toInt(),
            newExecutor(c.getConfig("executor"))
    )

    fun setItems(host: String, items: Collection<Item>) {
        val m = items.map { it.key to it }.toMap()
        hostItems.put(host, m)
    }

    override fun process(socket: Socket) {
        val req = parseJsonResponse<AgentRequest>(socket.getInputStream())
        val response = when (req.request) {
            "active checks" -> buildCheckResponse(req)
            "agent data" -> processDataRequest(req)
            else -> AgentResponse("failed", "invalid request")
        }
        JsonMapper.writeValue(socket.getOutputStream(), response)
    }

    private fun buildCheckResponse(req: AgentRequest): AgentResponse {
        val items = hostItems[req.host]
        if (items != null) {
            return AgentResponse("success", data = items.values, regexp = regexps)
        } else {
            return AgentResponse("failed", "host ${req.host} not found")
        }
    }

    private fun processDataRequest(req: AgentRequest): AgentResponse {
        req.data?.let {vs ->
            valueListener(vs)
            vs.forEach(this::updateItem)
        }
        return AgentResponse("success", "")
    }

    private fun updateItem(v: ItemValue) {
        hostItems[v.host]?.get(v.key)?.applyValue(v)
    }

}