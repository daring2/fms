package com.gitlab.daring.fms.zabbix.model

import java.time.Duration

data class ItemValue(
        val host: String,
        val key: String,
        val value: String,
        val time: Duration? = null,
        val isError: Boolean = false
)