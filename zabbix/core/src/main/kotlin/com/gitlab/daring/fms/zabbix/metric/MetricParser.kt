package com.gitlab.daring.fms.zabbix.metric

import com.gitlab.daring.fms.zabbix.model.Item

open class MetricParser {

    fun getMetric(item: Item): Metric {
        //TODO implement
        return Metric(item.key, emptyList(), item)
    }

    companion object {
        val Default = MetricParser()
    }

}