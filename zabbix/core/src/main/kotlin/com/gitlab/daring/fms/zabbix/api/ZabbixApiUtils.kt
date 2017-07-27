package com.gitlab.daring.fms.zabbix.api

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit.SECONDS

object ZabbixApiUtils {

    val DefaultHttpClient = createDefaultHttpClient()

    fun createDefaultHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .connectTimeout(3, SECONDS)
                .readTimeout(10, SECONDS)
                .writeTimeout(10, SECONDS)
                .build()
    }

}