package com.gitlab.daring.fms.zabbix.metric

import com.gitlab.daring.fms.zabbix.model.Item
import com.gitlab.daring.fms.zabbix.model.ItemValue
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec

class MetricSupplierTest : FunSpec({

    test("getValues") {
        val sup = object : MetricSupplier {
            override fun getCurrentValue(metric: Metric) =
                    ItemValue(metric.name + ".v", item = metric.item)
        }
        val metrics = listOf(
                Metric("m1", emptyList(), Item("i1")),
                Metric("m2", emptyList(), Item("i2"))
        )
        sup.getValues(metrics) shouldBe listOf(
                ItemValue("m1.v", item = metrics[0].item),
                ItemValue("m2.v", item = metrics[1].item)
        )
    }
    
})