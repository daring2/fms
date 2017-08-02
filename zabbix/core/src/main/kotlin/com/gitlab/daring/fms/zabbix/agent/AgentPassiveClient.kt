package com.gitlab.daring.fms.zabbix.agent

import com.gitlab.daring.fms.common.network.SocketProvider
import com.gitlab.daring.fms.common.network.SocketProviderImpl
import com.gitlab.daring.fms.zabbix.model.Item
import com.gitlab.daring.fms.zabbix.model.ItemValue
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.HeaderSize
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.ZbxError
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.ZbxNotSupported
import com.typesafe.config.Config

class AgentPassiveClient(
        val host: String,
        val port: Int = 10050,
        val socketProvider: SocketProvider = SocketProviderImpl(3000, 10000)
) {

    constructor(c: Config) :
            this(c.getString("host"), c.getInt("port"), SocketProviderImpl(c))


    fun request(item: Item): ItemValue {
        val socket = socketProvider.createSocket(host, port)
        socket.use {
            val req = "${item.key}\n".toByteArray(Charsets.UTF_8)
            socket.getOutputStream().write(req)
            val rbs = socket.getInputStream().readBytes()
            return parseResponse(item, rbs)
        }
    }

    fun request(itemKey: String) : ItemValue {
        return request(Item(itemKey))
    }

    private fun parseResponse(item: Item, bs: ByteArray): ItemValue {
        if (bs.size < HeaderSize)
            throw RuntimeException("invalid response")
        val str = String(bs, HeaderSize, bs.size - HeaderSize, item.charset)
        if (str == ZbxError)
            throw RuntimeException(str)
        val iv = ItemValue("", item.key, str)
        if (str.startsWith(ZbxNotSupported)) {
            val error = str.substring(ZbxNotSupported.length)
            return iv.copy(value = error, isError = true)
        }  else {
            return iv
        }
    }

}