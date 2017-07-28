package com.gitlab.daring.fms.common.json

import com.fasterxml.jackson.module.kotlin.readValue
import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration

class JsonUtilsTest {

    @Test
    fun testJsonMapper() {
        val b1 = TestBean("v1", 2)
        assertEquals(b1, JsonMapper.readValue<TestBean>("""{"p1": "v1", "p2": 2}"""))
        assertEquals(b1, JsonMapper.readValue<TestBean>("{p1: 'v1', p2: 2}"))
    }

    @Test
    fun testDurationSupport() {
        assertEquals(Duration.ofMillis(1), JsonMapper.readValue<Duration>("1"))
        assertEquals(Duration.ofMillis(1000), JsonMapper.readValue<Duration>("'1s'"))
        assertEquals("1000", JsonMapper.writeValueAsString(Duration.ofSeconds(1)))
    }

}

data class TestBean(
        val p1: String,
        val p2: Int
)