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

}