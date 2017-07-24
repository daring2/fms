package com.gitlab.daring.fms.common.json

import org.junit.Test
import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import org.junit.Assert.*

class JacksonExtensionTest {

    @Test
    fun testNodeToMap() {
        val n1 = JsonMapper.createObjectNode()
        n1.put("p1", "v1")
        n1.put("p2", 2)
        assertEquals(hashMapOf("p1" to "v1", "p2" to 2), n1.toMap())
    }

}