package com.gitlab.daring.fms.zabbix.agent.passive

import com.gitlab.daring.fms.common.concurrent.ConcurrentUtils.newExecutor
import com.gitlab.daring.fms.common.config.getMillis
import com.gitlab.daring.fms.zabbix.metric.ErrorMetricSupplier
import com.gitlab.daring.fms.zabbix.metric.MetricParser
import com.gitlab.daring.fms.zabbix.metric.MetricSupplier
import com.gitlab.daring.fms.zabbix.model.Item
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.HeaderSize
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.ZbxError
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.ZbxNotSupported
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.buildMessage
import com.gitlab.daring.fms.zabbix.util.ZabbixSocketServer
import com.typesafe.config.Config
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors.newFixedThreadPool

class AgentPassiveServer(
        override val port: Int = 10050,
        override val readTimeout: Int = 3000,
        override val executor: ExecutorService = newFixedThreadPool(2),
        val metricParser: MetricParser = MetricParser.Default
) : ZabbixSocketServer() {

    @Volatile
    var metricSupplier: MetricSupplier = ErrorMetricSupplier

    constructor(c: Config) : this(
            c.getInt("port"),
            c.getMillis("readTimeout").toInt(),
            newExecutor(c.getConfig("executor"))
    )

    override fun process(socket: Socket) {
        val line = socket.getInputStream().bufferedReader().readLine()
        val key = if (line.startsWith("ZBXD")) line.substring(HeaderSize) else line
        val result = try {
            val m = metricParser.getMetric(Item(key))
            val v = metricSupplier.getCurrentValue(m)
            if (v.isError) ZbxNotSupported + v.value else v.value
        } catch (e: Exception) {
            ZbxError
        }
        val response = buildMessage(result)
        socket.getOutputStream().write(response)
    }

}