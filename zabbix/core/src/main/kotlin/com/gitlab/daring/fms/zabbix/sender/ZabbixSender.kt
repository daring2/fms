package com.gitlab.daring.fms.zabbix.sender

import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import com.gitlab.daring.fms.common.network.SocketProvider
import com.gitlab.daring.fms.common.network.SocketProviderImpl
import com.gitlab.daring.fms.zabbix.model.ItemValue
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.parseJsonResponse
import com.typesafe.config.Config

class ZabbixSender(
        val host: String,
        val port: Int = 10051,
        val socketProvider: SocketProvider = SocketProviderImpl(3000, 10000)
) {

    constructor(c: Config) :
            this(c.getString("host"), c.getInt("port"), SocketProviderImpl(c))

    fun send(req: SendRequest): SendResult {
        val socket = socketProvider.createSocket(host, port)
        socket.use {
            val rn = req.buildJson()
            JsonMapper.writeValue(socket.getOutputStream(), rn)
            val rbs = socket.getInputStream().readBytes()
            return parseJsonResponse<SendResult>(rbs)
        }
    }

    fun send(vararg vs: ItemValue): SendResult {
        val req = SendRequest(listOf(*vs))
        return send(req)
    }

}