package com.gitlab.daring.fms.zabbix.metric

import com.gitlab.daring.fms.zabbix.model.ItemValue

object MetricUtils {

    /**
     * Creates [MetricSupplier] from specified function
     */
    fun newMetricSupplier(f: (Metric) -> ItemValue): MetricSupplier {
        return object : MetricSupplier {
            override fun getValue(metric: Metric)= f(metric)
        }
    }

    /**
     * Creates metric value from function result
     */
    fun newMetricValue(m: Metric, f: () -> Any): ItemValue {
        return try {
            ItemValue("" + f(), item = m.item)
        } catch (e: Exception) {
            ItemValue("$e", true, m.item)
        }
    }

}