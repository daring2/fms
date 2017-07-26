package com.gitlab.daring.fms.common.http

import com.typesafe.config.Config
import okhttp3.OkHttpClient

object HttpUtils {

    fun newHttpClient(c: Config): OkHttpClient {
        return OkHttpClient.Builder().config(c).build()
    }

}