package com.gitlab.daring.fms.common.json

import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import org.junit.Assert.*
import org.junit.Test
import com.fasterxml.jackson.module.kotlin.*

class JsonUtilsTest {

    @Test
    fun testJsonMapper() {
        val b1 = TestBean("v1", 2)
        assertEquals(b1, JsonMapper.readValue<TestBean>("""{"p1": "v1", "p2": 2}"""))
        assertEquals(b1, JsonMapper.readValue<TestBean>("{p1: 'v1', p2: 2}"))
    }

}

data class TestBean(
        val p1: String,
        val p2: Int
)