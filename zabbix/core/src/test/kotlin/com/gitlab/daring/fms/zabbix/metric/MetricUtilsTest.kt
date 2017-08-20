package com.gitlab.daring.fms.zabbix.metric

import com.gitlab.daring.fms.zabbix.metric.MetricUtils.newMetricSupplier
import com.gitlab.daring.fms.zabbix.metric.MetricUtils.newMetricValue
import com.gitlab.daring.fms.zabbix.model.Item
import com.gitlab.daring.fms.zabbix.model.ItemValue
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec

class MetricUtilsTest : FunSpec({

    test("newMetricSupplier") {
        val sup = newMetricSupplier { ItemValue(it.name + ".v") }
        val m1 = Metric("m1", emptyList())
        sup.getValue(m1) shouldBe ItemValue("m1.v")
    }

    test("newMetricValue") {
        val m1 = Metric("m1", emptyList(), Item("i1"))
        val v1 = newMetricValue(m1, { "v1" })
        v1 shouldBe ItemValue("v1", item = m1.item)
        val v2 = newMetricValue(m1, { throw RuntimeException("err1") })
        v2 shouldBe ItemValue("java.lang.RuntimeException: err1", true, item = m1.item)
    }

})