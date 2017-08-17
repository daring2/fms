package com.gitlab.daring.fms.zabbix.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Item(
        val key: String,
        val delay: Int = 60,
        val key_orig: String? = null,
        @Volatile
        var lastlogsize: Long = 0,
        @Volatile
        var mtime: Long = 0,
        @JsonIgnore
        val charset: String = "UTF-8",
        @JsonIgnore
        val host: String = ""
) {

    fun applyValue(v: ItemValue): Item {
        v.lastlogsize?.let { lastlogsize = it }
        v.mtime?.let { mtime = it }
        return this
    }

}