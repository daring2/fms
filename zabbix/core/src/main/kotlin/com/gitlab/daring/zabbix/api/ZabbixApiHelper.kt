package com.gitlab.daring.zabbix.api

import com.gitlab.daring.fms.common.config.getBean
import com.gitlab.daring.fms.common.http.HttpUtils.newHttpClient
import com.typesafe.config.Config
import okhttp3.OkHttpClient

class ZabbixApiHelper(
        val host: String,
        val authParams: AuthParams,
        val httpClient: OkHttpClient = ZabbixApiUtils.DefaultHttpClient
) {

    var url = "http://$host/zabbix/api_jsonrpc.php"

    constructor(c: Config): this(
            c.getString("host"),
            c.getBean("auth"),
            newHttpClient(c)
    )

    //TODO implement

}