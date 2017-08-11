package com.gitlab.daring.fms.zabbix.agent.active

import com.gitlab.daring.fms.zabbix.agent.active.CheckRegexpType.StringIncluded

data class CheckRegexp(
        val name: String,
        val expression: String,
        val expression_type: CheckRegexpType = StringIncluded,
        val exp_delimiter: String = ",",
        val case_sensitive: Int = 0
)