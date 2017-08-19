package com.gitlab.daring.fms.zabbix.metric

import com.gitlab.daring.fms.zabbix.model.ItemValue

object ErrorMetricSupplier : MetricSupplier {

    override fun getValue(metric: Metric): ItemValue {
        return ItemValue("Unsupported item key", true, metric.item)
    }

}