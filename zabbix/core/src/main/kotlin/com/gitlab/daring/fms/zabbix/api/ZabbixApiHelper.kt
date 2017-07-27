package com.gitlab.daring.fms.zabbix.api

import com.fasterxml.jackson.databind.JsonNode
import com.gitlab.daring.fms.common.config.getBean
import com.gitlab.daring.fms.common.http.HttpUtils.JsonMediaType
import com.gitlab.daring.fms.common.http.HttpUtils.newHttpClient
import com.gitlab.daring.fms.common.json.JsonUtils
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

    internal val authCache = AuthCache(
            authParams.tokenTimeout.toMillis(),
            this::login
    )

    val authToken: String
        get() = authCache.getToken()

    constructor(c: Config) : this(
            c.getString("host"),
            c.getBean("auth"),
            newHttpClient(c)
    )

    fun call(req: ZabbixApiRequest): JsonNode {
        return send(req.copy(auth = authToken))
    }

    fun call(method: String, params: Map<String, Any>): JsonNode {
        return call(ZabbixApiRequest(method, params))
    }

    private fun send(req: ZabbixApiRequest): JsonNode {
        val rb = RequestBody.create(JsonMediaType, req.toBytes())
        val r = Request.Builder().url(url).post(rb).build()
        httpClient.newCall(r).execute().use { cr ->
            if (!cr.isSuccessful) throw RuntimeException("code=${cr.code()}")
            val bs = cr.body()?.byteStream() ?: throw NullPointerException("body")
            val rn = JsonUtils.JsonMapper.readTree(bs)
            rn.get("error")?.let { throw RuntimeException("error=$it") }
            if (req.auth != null) authCache.updateAccessTime()
            return rn.get("result")
        }
    }

    private fun login(): String {
        val req = ZabbixApiRequest("user.login", authParams.apiParams)
        return send(req).textValue()
    }

}