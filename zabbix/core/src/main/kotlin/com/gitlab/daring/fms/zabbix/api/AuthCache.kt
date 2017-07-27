package com.gitlab.daring.fms.zabbix.api

internal class AuthCache(
        val cacheTime: Long,
        val login: () -> String,
        val currentTime: () -> Long = System::currentTimeMillis
) {

    private var token = ""
    @Volatile
    var accessTime = 0L

    fun getToken(): String = synchronized(this) {
        val ct = currentTime()
        if (token.isEmpty() || ct > accessTime + cacheTime) {
            token = login()
            accessTime = ct
        }
        token
    }

    fun updateAccessTime() {
        accessTime = currentTime()
    }

}