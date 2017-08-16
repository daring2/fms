package com.gitlab.daring.fms.zabbix.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ItemValue(
        val host: String,
        val key: String,
        val value: String,
        val state: Int = 0,
        val clock: Long? = null,
        val ns: Int? = null,
        val lastlogsize: Long? = null,
        val mtime: Long? = null
) {
    
    val isError @JsonIgnore get() = state == 1

    constructor(value: String, isError: Boolean):
            this("", "", value, if (isError) 1 else 0)

    fun withError(error: String): ItemValue {
        return copy(value = error, state = 1)
    }

    fun withTime(time: Instant?): ItemValue {
        return copy(clock = time?.epochSecond, ns = time?.nano)
    }
    
}