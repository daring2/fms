package com.gitlab.daring.fms.zabbix.agent.passive

import com.gitlab.daring.fms.common.network.SocketProvider
import com.gitlab.daring.fms.common.network.SocketProviderImpl
import com.gitlab.daring.fms.zabbix.model.Item
import com.gitlab.daring.fms.zabbix.model.ItemValue
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.ZbxError
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.ZbxNotSupported
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.parseResponse
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
            return processResponse(item, rbs)
        }
    }

    fun request(itemKey: String) : ItemValue {
        return request(Item(itemKey))
    }

    private fun processResponse(item: Item, bs: ByteArray): ItemValue {
        val str = parseResponse(bs, charset(item.charset))
        if (str == ZbxError)
            throw RuntimeException(str)
        val iv = ItemValue("", item.key, str)
        if (str.startsWith(ZbxNotSupported)) {
            val error = str.substring(ZbxNotSupported.length)
            return iv.withError(error)
        }  else {
            return iv
        }
    }

}