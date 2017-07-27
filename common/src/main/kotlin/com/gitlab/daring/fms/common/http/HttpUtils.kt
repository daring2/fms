package com.gitlab.daring.fms.common.http

import com.typesafe.config.Config
import okhttp3.MediaType
import okhttp3.OkHttpClient

object HttpUtils {

    val JsonMediaType = MediaType.parse("application/json; charset=utf-8")

    fun newHttpClient(c: Config): OkHttpClient {
        return OkHttpClient.Builder().config(c).build()
    }

}