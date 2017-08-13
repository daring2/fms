package com.gitlab.daring.fms.zabbix.agent.passive

import com.gitlab.daring.fms.zabbix.model.ItemValue
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.HeaderSize
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.ZbxError
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.ZbxNotSupported
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.buildMessage
import com.gitlab.daring.fms.zabbix.util.ZabbixSocketServer
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AgentPassiveServer(
        override val port: Int = 10050,
        override val readTimeout: Int = 3000,
        override val executor: ExecutorService = Executors.newFixedThreadPool(2)
) : ZabbixSocketServer() {

    //TODO refactor
    @Volatile
    var valueSupplier: (String) -> ItemValue = { key ->
        ItemValue("", key, "Unsupported item key", state = 1)
    }

    override fun process(socket: Socket) {
        val line = socket.getInputStream().bufferedReader().readLine()
        val key = if (line.startsWith("ZBXD")) line.substring(HeaderSize) else line
        val result = try {
            val v = valueSupplier(key)
            if (v.isError) ZbxNotSupported + v.value else v.value
        } catch (e: Exception) {
            ZbxError
        }
        val response = buildMessage(result)
        socket.getOutputStream().write(response)
    }

}