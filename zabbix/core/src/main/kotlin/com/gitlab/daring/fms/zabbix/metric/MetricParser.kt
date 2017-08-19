package com.gitlab.daring.fms.zabbix.metric

import com.gitlab.daring.fms.zabbix.model.Item

interface MetricParser {

    fun getMetric(item: Item): Metric

    companion object {
        val Default = DefaultMetricParser(1024)
    }

}