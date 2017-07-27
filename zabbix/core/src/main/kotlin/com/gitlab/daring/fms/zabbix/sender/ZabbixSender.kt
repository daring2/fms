package com.gitlab.daring.fms.zabbix.sender

import com.fasterxml.jackson.module.kotlin.readValue
import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import com.gitlab.daring.fms.common.network.SocketProvider
import com.gitlab.daring.fms.common.network.SocketProviderImpl
import com.gitlab.daring.fms.zabbix.sender.ZabbixSenderUtils.HeaderSize
import com.typesafe.config.Config

class ZabbixSender(
        val host: String,
        val port: Int = 10051,
        val socketProvider: SocketProvider = SocketProviderImpl(3000, 10000)
)  {

    constructor(c: Config) : this(c.getString("host"), c.getInt("port"))

    fun send(req: SendRequest): SendResult {
        val socket = socketProvider.createSocket(host, port)
        socket.use {
            val rn = req.buildJson()
            JsonMapper.writeValue(socket.getOutputStream(), rn)
            val rbs = socket.getInputStream().readBytes()
            return parseResult(rbs)
        }
    }

    fun send(vararg vs: ItemValue): SendResult {
        val req = SendRequest(listOf(*vs))
        return send(req)
    }

    private fun parseResult(bs: ByteArray): SendResult {
        if (bs.size < HeaderSize)
            throw RuntimeException("invalid result")
        val str = String(bs, HeaderSize, bs.size - HeaderSize, Charsets.UTF_8)
        return JsonMapper.readValue(str)
    }

}