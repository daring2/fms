package com.gitlab.daring.fms.common.http

import com.gitlab.daring.fms.common.config.getMillis
import com.typesafe.config.Config
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit.MILLISECONDS

fun OkHttpClient.Builder.config(c: Config): OkHttpClient.Builder {
    return this
            .connectTimeout(c.getMillis("connectTimeout"), MILLISECONDS)
            .readTimeout(c.getMillis("readTimeout"), MILLISECONDS)
            .writeTimeout(c.getMillis("writeTimeout"), MILLISECONDS)
            .retryOnConnectionFailure(c.getBoolean("retryOnConnectionFailure"))
}