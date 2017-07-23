package com.gitlab.daring.zabbix.sender

import java.time.Duration

data class SendRequest(
        val data: List<ItemValue>,
        val time: Duration? = null
)