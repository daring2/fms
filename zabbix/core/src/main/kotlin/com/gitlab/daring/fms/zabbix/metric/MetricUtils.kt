package com.gitlab.daring.fms.zabbix.metric

import com.gitlab.daring.fms.zabbix.model.ItemValue

object MetricUtils {

    /**
     * Creates [MetricSupplier] from specified function
     */
    fun newMetricSupplier(f: (Metric) -> ItemValue): MetricSupplier {
        return object : MetricSupplier {
            override fun getCurrentValue(metric: Metric)= f(metric)
        }
    }

    /**
     * Creates metric value from function result
     */
    fun newMetricValue(m: Metric, f: () -> Any): ItemValue {
        return try {
            val r = f()
            when (r) {
                is ItemValue -> r
                is Exception -> ItemValue("$r", true, m.item)
                else -> ItemValue("$r", false, m.item)
            }
        } catch (e: Exception) {
            ItemValue("$e", true, m.item)
        }
    }

}