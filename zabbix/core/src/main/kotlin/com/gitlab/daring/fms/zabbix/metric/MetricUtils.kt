package com.gitlab.daring.fms.zabbix.metric

import com.gitlab.daring.fms.zabbix.model.ItemValue

object MetricUtils {

    fun newMetricSupplier(f: (Metric) -> ItemValue): MetricSupplier {
        return object : MetricSupplier {
            override fun getValue(metric: Metric)= f(metric)
        }
    }

}