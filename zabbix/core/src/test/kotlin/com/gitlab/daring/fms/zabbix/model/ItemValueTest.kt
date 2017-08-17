package com.gitlab.daring.fms.zabbix.model

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec
import java.time.Instant

class ItemValueTest : FunSpec({

    test("constructors") {
        val v0 = ItemValue("", "", "")

        val v1 = ItemValue("v1", false)
        v1 shouldBe v0.copy(value = "v1", state = 0)

        val v2 = ItemValue("v2", true)
        v2 shouldBe v0.copy(value = "v2", state = 1)

        val v3 = ItemValue("v3", false, Item("i1", key_orig = "oi1", host = "h1"))
        v3 shouldBe ItemValue("h1", "oi1", "v3")
    }

    test("withError") {
        val v1 = ItemValue("h1", "i1", "v1")
        v1.state shouldBe 0
        v1.value shouldBe "v1"
        v1.isError shouldBe false

        val v2 = v1.withError("err1")
        v2.state shouldBe 1
        v2.value shouldBe "err1"
        v2.isError shouldBe true
    }

    test("withTime") {
        val v1 = ItemValue("h1", "i1", "v1").withTime(null)
        v1.clock shouldBe null
        v1.ns shouldBe null

        val v2 = v1.withTime(Instant.ofEpochMilli(2003))
        v2.clock shouldBe 2L
        v2.ns shouldBe 3000000
    }

})