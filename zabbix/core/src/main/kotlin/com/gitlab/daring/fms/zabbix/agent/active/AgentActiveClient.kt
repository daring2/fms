package com.gitlab.daring.fms.zabbix.agent.active

import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import com.gitlab.daring.fms.zabbix.model.Item
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.parseJsonResponse
import org.slf4j.LoggerFactory.getLogger
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors.newCachedThreadPool
import java.util.concurrent.atomic.AtomicBoolean

class AgentActiveClient(
        val port: Int = 10051,
        val readTimeout: Int = 3000,
        val executor: ExecutorService = newCachedThreadPool()
) : AutoCloseable {

    private val logger = getLogger(javaClass)
    private val started = AtomicBoolean()
    private val hostItems = ConcurrentHashMap<String, List<Item>>()

    @Volatile
    var listener:  (List<AgentItemValue>) -> Unit = {}

    val isStarted get() = started.get()

    fun start() {
        if (started.compareAndSet(false, true))
            executor.execute(this::run)
    }

    fun stop() {
        started.compareAndSet(true, false)
    }

    override fun close() = stop()

    fun setItems(host: String, items: List<Item>) {
        hostItems.put(host, ArrayList(items))
    }

    private fun run() {
        ServerSocket(port).use { server ->
            while (isStarted) tryRun("accept") {
                val socket = server.accept()
                executor.execute { processRequest(socket) }
            }
        }
    }

    private fun processRequest(socket: Socket) = tryRun("process request") {
        socket.use {
            it.soTimeout = readTimeout
            val req = parseJsonResponse<AgentRequest>(it.getInputStream())
            val response = when (req.request) {
                "active checks" -> buildCheckResponse(req)
                "agent data" -> processDataRequest(req)
                else -> AgentResponse("failed", "invalid request")
            }
            JsonMapper.writeValue(it.getOutputStream(), response)
        }
    }

    private fun buildCheckResponse(req: AgentRequest): AgentResponse {
        val items = hostItems.get(req.host)
        if (items != null) {
            return AgentResponse("success", data = items)
        } else {
            return AgentResponse("failed", "host ${req.host} not found")
        }
    }

    private fun processDataRequest(req: AgentRequest): AgentResponse {
        req.data?.let { listener(it) }
        return AgentResponse("success", "")
    }

    private fun tryRun(action: String, f: () -> Unit) {
        try {
            f()
        } catch (e: Exception) {
            logger.warn("$action error", e)
        }
    }

}