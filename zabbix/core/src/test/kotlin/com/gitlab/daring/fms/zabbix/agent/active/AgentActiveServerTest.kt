package com.gitlab.daring.fms.zabbix.agent.active

import com.gitlab.daring.fms.common.config.ConfigUtils.configFromString
import com.gitlab.daring.fms.common.network.SocketProviderImpl
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec

class AgentActiveServerTest : FunSpec({

    test("create from config") {
        val c1 = configFromString("{server=\"h1:10\", connectTimeout=1s, readTimeout=2s}")
        val srv1 = AgentActiveServer.create(c1)
        srv1.serverHost shouldBe "h1"
        srv1.serverPort shouldBe 10
        val sp = srv1.socketProvider as SocketProviderImpl
        sp.connectTimeout shouldBe 1000
        sp.readTimeout shouldBe 2000

        val c2 = configFromString("{server=h1, connectTimeout=1s, readTimeout=2s}")
        val srv2 = AgentActiveServer.create(c2)
        srv2.serverHost shouldBe "h1"
        srv2.serverPort shouldBe 10051
    }

    //TODO implement

})