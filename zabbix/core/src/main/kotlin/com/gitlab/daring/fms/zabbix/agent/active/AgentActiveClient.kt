package com.gitlab.daring.fms.zabbix.agent.active

import com.gitlab.daring.fms.common.concurrent.ConcurrentUtils.newExecutor
import com.gitlab.daring.fms.common.config.getMillis
import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import com.gitlab.daring.fms.zabbix.model.Item
import com.gitlab.daring.fms.zabbix.model.ItemValue
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.parseJsonResponse
import com.typesafe.config.Config
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
    private var serverSocket: ServerSocket? = null

    /**
     * Listener of new item values received from Zabbix agent
     */
    @Volatile
    var valueListener:  (List<ItemValue>) -> Unit = {}

    val isStarted get() = started.get()

    constructor(c: Config) : this(
            c.getInt("port"),
            c.getMillis("readTimeout").toInt(),
            newExecutor(c.getConfig("executor"))
    )

    fun start() {
        if (started.compareAndSet(false, true))
            executor.execute(this::run)
    }

    fun stop() {
        if (started.compareAndSet(true, false))
            serverSocket?.close()
    }

    override fun close() = stop()

    fun setItems(host: String, items: List<Item>) {
        hostItems.put(host, ArrayList(items))
    }

    private fun run() {
        ServerSocket(port).use {
            serverSocket = it
            while (isStarted) tryRun("accept") {
                val socket = it.accept()
                executor.execute { processRequest(socket) }
            }
        }
    }

    private fun processRequest(socket: Socket) = tryRun("process") {
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
        //TODO update lastlogsize, mtime
        req.data?.let { valueListener(it) }
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