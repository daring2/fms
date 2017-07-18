package com.gitlab.daring.zabbix.sender

import javax.net.SocketFactory

interface ZabbixSender {

}

class ZabbixSenderImpl(
        val host: String,
        val port: Int = 10051,
        val socketFactory: SocketFactory = SocketFactory.getDefault()
): ZabbixSender {
    
}