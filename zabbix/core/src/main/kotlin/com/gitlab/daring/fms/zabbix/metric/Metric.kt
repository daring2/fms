package com.gitlab.daring.fms.zabbix.metric

import com.gitlab.daring.fms.zabbix.model.Item

data class Metric(
        val name: String,
        val params: List<String>,
        val item: Item? = null
)