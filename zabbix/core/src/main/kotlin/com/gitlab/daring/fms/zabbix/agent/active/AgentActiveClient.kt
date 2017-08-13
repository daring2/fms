package com.gitlab.daring.fms.zabbix.agent.active

import com.gitlab.daring.fms.common.concurrent.ConcurrentUtils.newExecutor
import com.gitlab.daring.fms.common.config.getMillis
import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import com.gitlab.daring.fms.common.network.ServerSocketProvider
import com.gitlab.daring.fms.zabbix.model.Item
import com.gitlab.daring.fms.zabbix.model.ItemValue
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.parseJsonResponse
import com.typesafe.config.Config
import org.slf4j.LoggerFactory.getLogger
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors.newFixedThreadPool
import java.util.concurrent.atomic.AtomicBoolean

class AgentActiveClient(
        val port: Int = 10051,
        val readTimeout: Int = 3000,
        val executor: ExecutorService = newFixedThreadPool(2),
        val socketProvider: ServerSocketProvider = ServerSocketProvider()
) : AutoCloseable {

    private val logger = getLogger(javaClass)

    private val started = AtomicBoolean()
    private val hostItems = ConcurrentHashMap<String, Map<String, Item>>()
    @Volatile
    internal var serverSocket: ServerSocket? = null

    /**
     * Global regular expressions
     */
    @Volatile
    var regexps: List<CheckRegexp>? = null

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
        if (started.compareAndSet(true, false)) {
            serverSocket?.close()
            serverSocket = null
        }
    }

    override fun close() = stop()

    fun setItems(host: String, items: Collection<Item>) {
        val m = items.map { it.key to it }.toMap()
        hostItems.put(host, m)
    }

    private fun run() {
        socketProvider.createServerSocket(port).use {
            serverSocket = it
            while (isStarted && !it.isClosed) {
                tryRun("accept") {
                    val socket = it.accept()
                    executor.execute { processRequest(socket) }
                }
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

    private fun tryRun(action: String, f: () -> Unit) {
        try {
            f.invoke()
        } catch (e: Exception) {
            if (isStarted) logger.warn("$action error", e)
        }
    }

}