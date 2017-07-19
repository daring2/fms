package com.gitlab.daring.zabbix.sender

import com.gitlab.daring.fms.common.network.ConfigurableSocketFactory
import com.gitlab.daring.fms.common.network.SocketProvider
import com.typesafe.config.Config

interface ZabbixSender {

}

class ZabbixSenderImpl(
        val host: String,
        val port: Int = 10051,
        val socketProvider: SocketProvider = ConfigurableSocketFactory(3000, 10000)
): ZabbixSender {

    constructor(c: Config) : this(c.getString("host"), c.getInt("port"))

}