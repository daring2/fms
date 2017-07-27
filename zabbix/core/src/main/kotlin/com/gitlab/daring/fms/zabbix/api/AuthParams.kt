package com.gitlab.daring.fms.zabbix.api

import java.time.Duration

data class AuthParams(
        val user: String,
        val password: String,
        val authTimeout: Duration
)