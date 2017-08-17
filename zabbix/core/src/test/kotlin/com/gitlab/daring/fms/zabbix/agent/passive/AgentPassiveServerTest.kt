package com.gitlab.daring.fms.zabbix.agent.passive

import com.gitlab.daring.fms.common.config.ConfigUtils.configFromString
import com.gitlab.daring.fms.common.util.closeQuietly
import com.gitlab.daring.fms.zabbix.metric.MetricUtils.newMetricSupplier
import com.gitlab.daring.fms.zabbix.model.ItemValue
import com.gitlab.daring.fms.zabbix.util.MockSocketProvider
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.ZbxError
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.ZbxNotSupported
import com.gitlab.daring.fms.zabbix.util.ZabbixProtocolUtils.buildMessage
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec
import org.junit.Assert.assertArrayEquals
import org.mockito.Mockito
import java.util.concurrent.ThreadPoolExecutor

class AgentPassiveServerTest : FunSpec() {

    init {

        test("constructors") {
            val c = configFromString("{ port=1, readTimeout=2s,  executor { size=3, maxSize=4 }}")
            val cl = AgentPassiveServer(c)
            cl.port shouldBe 1
            cl.readTimeout shouldBe 2000
            val exec = cl.executor as ThreadPoolExecutor
            exec.corePoolSize shouldBe 3
            exec.maximumPoolSize shouldBe 4
        }

        testWithContext("process") {
            srv.start()
            checkProcess("i1", "i1.v")
            checkProcess("err1", "${ZbxNotSupported}err1.v")
            checkProcess("fail1", ZbxError)
        }

        //TODO test metric parser usage

    }

    internal fun testWithContext(name: String, f: TestContext.() -> Unit) {
        test(name) { TestContext().use(f) }
    }

    internal class TestContext : AutoCloseable {

        val sp = MockSocketProvider()
        val srv = AgentPassiveServer(10, 100)

        init {
            srv.socketProvider = sp.serverProvider
            srv.metricSupplier = newMetricSupplier { m ->
                val key = m.name
                if (key.contains("fail")) throw RuntimeException(key)
                ItemValue("$key.v", key.contains("err"))
            }
        }

        fun checkProcess(req: String, res: String) {
            sp.newSocket()
            sp.setInput(req)
            sp.accept()
            val expOut = buildMessage(res)
            assertArrayEquals(expOut, sp.output().toByteArray())
            Mockito.verify(sp.socket).close()
        }

        override fun close() {
            srv.closeQuietly()
            sp.closeQuietly()
        }
    }

}