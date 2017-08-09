package com.gitlab.daring.fms.zabbix.model

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec

class ItemTest : FunSpec({

    test("applyValue") {
        val i1 = Item("i1", lastlogsize = 1, mtime = 2)
        val v1 = ItemValue("h1", "i1", "v1")
        i1.copy().applyValue(v1) shouldBe i1
        val v2 = v1.copy(lastlogsize = 11, mtime = 12)
        i1.copy().applyValue(v2) shouldBe i1.copy(lastlogsize = 11, mtime = 12)
    }

})