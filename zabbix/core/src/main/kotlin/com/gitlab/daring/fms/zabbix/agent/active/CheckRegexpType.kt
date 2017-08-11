package com.gitlab.daring.fms.zabbix.agent.active

import com.fasterxml.jackson.annotation.JsonValue

enum class CheckRegexpType {

    StringIncluded,
    AnyStringIncluded,
    StringExcluded,
    Match,
    NotMatch;

    @JsonValue
    fun code() = ordinal

}