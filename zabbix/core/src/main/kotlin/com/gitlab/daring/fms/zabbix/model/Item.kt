package com.gitlab.daring.fms.zabbix.model

import com.fasterxml.jackson.annotation.JsonIgnore

data class Item(
        val key: String,
        val delay: Int = 60,
        @Volatile
        var lastlogsize: Long = 0,
        @Volatile
        var mtime: Long = 0,
        @JsonIgnore
        val charset: String = "UTF-8"
) {

    fun applyValue(v: ItemValue): Item {
        v.lastlogsize?.let { lastlogsize = it }
        v.mtime?.let { mtime = it }
        return this
    }

}