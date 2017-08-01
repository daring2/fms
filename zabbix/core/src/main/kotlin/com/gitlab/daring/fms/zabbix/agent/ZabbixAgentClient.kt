package com.gitlab.daring.fms.zabbix.agent

import com.gitlab.daring.fms.common.network.SocketProvider
import com.gitlab.daring.fms.common.network.SocketProviderImpl
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.HeaderSize
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.ZbxError
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.ZbxNotSupported
import com.typesafe.config.Config

class ZabbixAgentClient(
        val host: String,
        val port: Int = 10050,
        val socketProvider: SocketProvider = SocketProviderImpl(3000, 10000)
) {

    constructor(c: Config) :
            this(c.getString("host"), c.getInt("port"), SocketProviderImpl(c))


    fun request(itemKey: String): String {
        val socket = socketProvider.createSocket(host, port)
        socket.use {
            val req = "$itemKey\n".toByteArray(Charsets.UTF_8)
            socket.getOutputStream().write(req)
            val rbs = socket.getInputStream().readBytes()
            return parseResponse(rbs)
        }
    }


    private fun parseResponse(bs: ByteArray): String {
        if (bs.size < HeaderSize)
            throw RuntimeException("invalid response")
        val str = String(bs, HeaderSize, bs.size - HeaderSize, Charsets.UTF_8)
        if (str == ZbxError)
            throw RuntimeException(str)
        if (str.startsWith(ZbxNotSupported)) {
            val error = str.substring(ZbxNotSupported.length)
            throw ZabbixNotSupportedException(error)
        }
        return str
    }

}