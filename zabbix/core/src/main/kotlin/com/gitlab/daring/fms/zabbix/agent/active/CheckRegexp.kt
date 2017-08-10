package com.gitlab.daring.fms.zabbix.agent.active

data class CheckRegexp(
        val name: String,
        val expression: String,
        val expression_type: Int = 0,
        val exp_delimiter: String = ",",
        val case_sensitive: Int = 0
)