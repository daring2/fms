package com.gitlab.daring.fms.common.json

import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec

class JacksonExtensionTest : FunSpec({

    test("Node.toMap") {
        val n1 = JsonMapper.createObjectNode()
        n1.put("p1", "v1")
        n1.put("p2", 2)
        n1.toMap() shouldBe hashMapOf("p1" to "v1", "p2" to 2)
    }

    test("ObjectMapper.readList") {
        JsonMapper.readList<Any>("['v1', 2]") shouldBe listOf("v1", 2)
        JsonMapper.readList<String>("['v1', 2]") shouldBe listOf("v1", "2")
    }

})