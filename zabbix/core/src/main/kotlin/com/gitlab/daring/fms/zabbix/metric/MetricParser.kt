package com.gitlab.daring.fms.zabbix.metric

import com.gitlab.daring.fms.zabbix.model.Item

class MetricParser {

    fun parseKey(item: Item): Metric {
        //TODO implement
        return Metric(item.key, emptyList(), item)
    }

    companion object {
        val Default = MetricParser()
    }

}