package com.gitlab.daring.fms.zabbix.api

import java.time.Duration

data class AuthParams(
        val user: String,
        val password: String,
        val tokenTimeout: Duration
) {

    val apiParams = mapOf("user" to user, "password" to password)

}