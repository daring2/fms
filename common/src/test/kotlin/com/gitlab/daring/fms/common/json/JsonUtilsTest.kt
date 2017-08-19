package com.gitlab.daring.fms.common.json

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.module.kotlin.readValue
import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import com.gitlab.daring.fms.common.json.JsonUtils.YamlMapper
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec
import java.time.Duration

class JsonUtilsTest: FunSpec({

    test("JsonMapper") {
        val b1 = TestBean("v1", 2)
        JsonMapper.readValue<TestBean>("""{"p1": "v1", "p2": 2}""") shouldBe b1
        JsonMapper.readValue<TestBean>("{p1: 'v1', p2: 2}") shouldBe b1
    }

    test("duration support") {
        JsonMapper.readValue<Duration>("10") shouldBe Duration.ofMillis(10)
        JsonMapper.readValue<Duration>("'1s'") shouldBe Duration.ofMillis(1000)
        JsonMapper.writeValueAsString(Duration.ofSeconds(1)) shouldBe "1000"
    }

    test("enum support") {
        JsonMapper.writeValueAsString(TestEnum.E1) shouldBe "1"
        JsonMapper.readValue<TestEnum>("1") shouldBe TestEnum.E1
    }

    test("YamlMapper") {
        YamlMapper.readList<String>("[v1, 2]") shouldBe listOf("v1", "2")
        YamlMapper.readValue<Any>("[v1, p2 : v2, 3]") shouldBe listOf(
                "v1", mapOf("p2" to "v2"), 3
        )
    }

})

data class TestBean(
        val p1: String,
        val p2: Int
)

enum class TestEnum  {
    E0, E1;

    @JsonValue
    fun code() = ordinal

}