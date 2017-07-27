package com.gitlab.daring.fms.zabbix.api

import com.fasterxml.jackson.databind.JsonNode
import com.gitlab.daring.fms.common.config.getBean
import com.gitlab.daring.fms.common.http.HttpUtils.JsonMediaType
import com.gitlab.daring.fms.common.http.HttpUtils.newHttpClient
import com.typesafe.config.Config
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

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

    private fun login(): String {
        val req = ZabbixApiRequest("user.login", authParams.apiParams)
        return send(req).textValue()
    }

    private fun send(req: ZabbixApiRequest): JsonNode {
        val rb = RequestBody.create(JsonMediaType, req.toBytes())
        val r = Request.Builder().url(url).post(rb).build()
        httpClient.newCall(r).execute().use { cr ->
            if (!cr.isSuccessful) throw RuntimeException("code=${cr.code()}")
            //TODO parse result, check error, update lastAccess time
            TODO("not implemented")
        }
    }

    //TODO implement

}