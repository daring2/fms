package com.gitlab.daring.fms.zabbix.agent.active

import com.gitlab.daring.fms.zabbix.agent.active.AgentResponse.Companion.Failed
import com.gitlab.daring.fms.zabbix.agent.active.AgentResponse.Companion.Success
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec

class AgentResponseTest : FunSpec({

    test("success") {
        AgentResponse(Success).success shouldBe true
        AgentResponse(Failed).success shouldBe false
        AgentResponse("").success shouldBe false
    }

})