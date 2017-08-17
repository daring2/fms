package com.gitlab.daring.fms.zabbix.metric

import com.gitlab.daring.fms.zabbix.model.Item
import com.gitlab.daring.fms.zabbix.model.ItemValue
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec

class ErrorMetricSupplierTest : FunSpec({

    test("getValue") {
        val sup = ErrorMetricSupplier
        val i1 = Item("i1")
        val v1 = sup.getValue(Metric("m1", emptyList(), i1))
        v1 shouldBe ItemValue("Unsupported item key", true, i1)
    }

})