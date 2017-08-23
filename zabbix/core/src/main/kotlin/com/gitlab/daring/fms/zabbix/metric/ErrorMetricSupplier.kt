package com.gitlab.daring.fms.zabbix.metric

import com.gitlab.daring.fms.zabbix.model.ItemValue

/**
 * Implementation of [MetricSupplier] that returns error values for all metrics
 */
object ErrorMetricSupplier : MetricSupplier {

    override fun getCurrentValue(metric: Metric): ItemValue {
        return ItemValue("Unsupported item key", true, metric.item)
    }

}