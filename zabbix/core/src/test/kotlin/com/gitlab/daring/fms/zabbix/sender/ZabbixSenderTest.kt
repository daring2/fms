package com.gitlab.daring.fms.zabbix.sender

import com.gitlab.daring.fms.common.config.ConfigUtils.configFromString
import com.gitlab.daring.fms.common.network.SocketProviderImpl
import com.gitlab.daring.fms.zabbix.model.ItemValue
import com.gitlab.daring.fms.zabbix.util.MockSocketProvider
import com.gitlab.daring.fms.zabbix.util.ZabbixTestUtils.TestHeader
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec
import org.junit.Test
import org.mockito.Mockito.verify

class ZabbixSenderTest: FunSpec({

    test("init") {
        val c = configFromString("{ host=h1, port=10, connectTimeout=1s, readTimeout=2s }")
        val sender = ZabbixSender(c)
        sender.host shouldBe "h1"
        sender.port shouldBe 10
        val sp = sender.socketProvider as SocketProviderImpl
        sp.connectTimeout shouldBe 1000
        sp.readTimeout shouldBe 2000
    }

    @Test
    fun testSend() {
        val sp = MockSocketProvider()
        val sr = SendResult("r1", "info1")
        sp.setJsonInput(TestHeader, sr)
        val sender = ZabbixSender("h1", 10, sp.provider)

        val v1 = ItemValue("h1", "k1", "v1")
        val req1 = SendRequest(listOf(v1))
        sender.send(req1) shouldBe sr
        verify(sp.provider).createSocket("h1", 10)
        sp.assertJsonOutput(req1.buildJson())
        verify(sp.socket).close()
    }

})