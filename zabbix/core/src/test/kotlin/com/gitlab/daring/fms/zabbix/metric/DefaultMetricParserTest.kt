package com.gitlab.daring.fms.zabbix.metric

import com.gitlab.daring.fms.zabbix.model.Item
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec

class DefaultMetricParserTest : FunSpec({

    test("getMetric") {
        val p = DefaultMetricParser(16)
        val i1 = Item("i1")
        p.getMetric(i1) shouldBe Metric("i1", emptyList(), i1)
        val i2 = Item("i2[p1, p2]")
        p.getMetric(i2) shouldBe Metric("i2", listOf("p1", "p2"), i2)
    }

    test("cache") {
        val p = DefaultMetricParser(16)
        val i1 = Item("i1[p1]")
        val m1 = p.getMetric(i1)
        (p.getMetric(i1).name === m1.name) shouldBe true
        p.cache.invalidateAll()
        (p.getMetric(i1).name === m1.name) shouldBe false
    }

})