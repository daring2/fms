package com.gitlab.daring.fms.zabbix.agent.passive

import com.gitlab.daring.fms.common.config.getHostAndPort
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

    fun request(item: Item): ItemValue {
        val socket = socketProvider.createSocket(host, port)
        socket.use {
            val req = "${item.key}\n".toByteArray()
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

    companion object {

        operator fun invoke(c: Config): AgentPassiveClient {
            val hp = c.getHostAndPort("server").withDefaultPort(10050)
            return AgentPassiveClient(hp.host, hp.port, SocketProviderImpl(c))
        }

    }

}