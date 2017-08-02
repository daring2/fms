package com.gitlab.daring.fms.zabbix.model

import com.fasterxml.jackson.annotation.JsonIgnore

data class Item(
        val key: String,
        val delay: Int = 60,
        val lastlogsize: Long = 0,
        val mtime: Long = 0,
        @JsonIgnore
        val charset: String = "UTF-8"
)