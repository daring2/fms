package com.gitlab.daring.fms.zabbix.metric

import com.gitlab.daring.fms.zabbix.model.ItemValue

/**
 * Supplier of metric values
 */
interface MetricSupplier {

    /**
     * Returns current value of specified metric
     */
    fun getCurrentValue(metric: Metric): ItemValue

    /**
     * Returns values for specified metrics
     */
    fun getValues(metris: List<Metric>): List<ItemValue> {
        return metris.map(this::getCurrentValue)
    }

}