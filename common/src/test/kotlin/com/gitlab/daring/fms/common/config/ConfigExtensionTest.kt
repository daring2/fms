package com.gitlab.daring.fms.common.config

import com.gitlab.daring.fms.common.config.ConfigUtils.configFromString
import com.google.common.net.HostAndPort
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec

class ConfigExtensionTest : FunSpec({

    test("getMillis") {
        configFromString("p=10").getMillis("p") shouldBe 10L
        configFromString("p=10s").getMillis("p") shouldBe 10000L
        configFromString("p=10m").getMillis("p") shouldBe 600000L
    }

    test("toMap") {
        val c1 = configFromString("p1=v1,p2=2")
        hashMapOf("p1" to "v1", "p2" to 2) shouldBe c1.toMap()
    }

    test("toBean") {
        val c1 = configFromString("b1 { p1=v1,p2=2 }")
        val b1 = TestBean("v1", 2)
        c1.getConfig("b1").toBean<TestBean>() shouldBe b1
        c1.getBean<TestBean>("b1") shouldBe b1
    }

    test("getOpt") {
        val c1 = configFromString("p1=10")
        c1.getOpt { getInt("p1") } shouldBe 10
        c1.getOpt { getInt("p2") } shouldBe null
    }

    test("getHostAndPort") {
        val cl = configFromString("p1=\"h1:10\"")
        cl.getHostAndPort("p1") shouldBe HostAndPort.fromParts("h1", 10)
    }

})

data class TestBean(
        val p1: String,
        val p2: Int
)