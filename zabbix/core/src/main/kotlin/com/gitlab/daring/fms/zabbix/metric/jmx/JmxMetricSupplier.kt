package com.gitlab.daring.fms.zabbix.metric.jmx

import com.gitlab.daring.fms.zabbix.metric.ErrorMetricSupplier
import com.gitlab.daring.fms.zabbix.metric.Metric
import com.gitlab.daring.fms.zabbix.metric.MetricSupplier
import com.gitlab.daring.fms.zabbix.metric.MetricUtils.newMetricValue
import com.gitlab.daring.fms.zabbix.model.ItemValue
import java.lang.management.ManagementFactory
import javax.management.MBeanServerConnection
import javax.management.ObjectName

class JmxMetricSupplier(
        val connectionSupplier: () -> JmxConnection = ManagementFactory::getPlatformMBeanServer
) : MetricSupplier {

    override fun getValue(metric: Metric): ItemValue {
        //TODO implement jmx.invoke metric
        return when (metric.name) {
            "jmx.get" -> getAttribute(metric)
            else -> ErrorMetricSupplier.getValue(metric)
        }
    }

    private fun getAttribute(m: Metric) = newMetricValue(m) {
        require(m.params.size == 2, { "metric requires 2 parameters" })
        val con = connectionSupplier()
        val mbn = ObjectName(m.params[0])
        //TODO support CompositeData
        con.getAttribute(mbn, m.params[1])
    }

}

typealias JmxConnection = MBeanServerConnection