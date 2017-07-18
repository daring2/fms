package com.gitlab.daring.zabbix.sender

import com.typesafe.config.Config
import javax.net.SocketFactory

interface ZabbixSender {

}

class ZabbixSenderImpl(
        val host: String,
        val port: Int = 10051,
        val socketFactory: SocketFactory = SocketFactory.getDefault()
): ZabbixSender {

    constructor(c: Config) : this(c.getString("host"), c.getInt("port"))

}