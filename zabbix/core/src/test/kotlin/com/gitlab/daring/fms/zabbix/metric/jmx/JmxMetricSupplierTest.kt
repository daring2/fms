package com.gitlab.daring.fms.zabbix.metric.jmx

import com.gitlab.daring.fms.zabbix.metric.Metric
import com.gitlab.daring.fms.zabbix.model.Item
import com.gitlab.daring.fms.zabbix.model.ItemValue
import io.kotlintest.matchers.shouldBe
import io.kotlintest.mock.`when`
import io.kotlintest.mock.mock
import io.kotlintest.specs.FunSpec
import java.lang.management.ManagementFactory
import javax.management.MBeanServerConnection
import javax.management.ObjectName

class JmxMetricSupplierTest : FunSpec({

    test("jmx.get") {
        val con = mock<MBeanServerConnection>()
        val sup = JmxMetricSupplier({ con })
        val bn1 = "d1:name=b1"
        `when`(con.getAttribute(ObjectName(bn1), "a1")).thenReturn("v1")
        val err2 = RuntimeException("err2")
        `when`(con.getAttribute(ObjectName(bn1), "a2")).thenThrow(err2)

        val m1 = Metric("jmx.get", listOf(bn1, "a1"), Item("i1"))
        sup.getValue(m1) shouldBe ItemValue("v1", false, m1.item)

        val m2 = m1.copy(params = listOf(bn1, "a2"))
        sup.getValue(m2) shouldBe ItemValue("$err2", true, m1.item)

        val m3 = m1.copy(params = listOf(bn1))
        val err3 = IllegalArgumentException("metric requires 2 parameters")
        sup.getValue(m3) shouldBe ItemValue("$err3", true, m1.item)
    }

    test("local mbean server") {
        val sup = JmxMetricSupplier()
        val m1 = Metric("jmx.get", listOf("java.lang:type=Runtime", "Name"), Item("i1"))
        val vmName = ManagementFactory.getRuntimeMXBean().name
        sup.getValue(m1) shouldBe ItemValue(vmName, false, m1.item)
    }

})