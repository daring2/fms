package com.gitlab.daring.zabbix.sender

import java.time.Duration

data class ItemValue(
        val host: String,
        val key: String,
        val value: String,
        val time: Duration? = null,
        val isError: Boolean = false
) {

}