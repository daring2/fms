package com.gitlab.daring.fms.zabbix.api

import com.gitlab.daring.fms.common.http.HttpUtils.JsonMediaType
import io.kotlintest.matchers.shouldBe
import okhttp3.*
import okio.Buffer
import java.util.*

class MockHttpClient: Interceptor {

    val requests = LinkedList<Request>()
    val responses = LinkedList<Response>()
    val client = buildClient()

    var expUrl = ""
    var expMethod = "POST"

    private fun buildClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(this)
                .build()
    }

    fun addResponse(code: Int, body: String) {
        responses.add(newResponse(code, body))
    }

    fun newResponse(code: Int, body: String): Response {
        return Response.Builder()
                .request(Request.Builder().url("http://url1").build())
                .protocol(Protocol.HTTP_2)
                .code(code).message("OK")
                .body(ResponseBody.create(JsonMediaType, body))
                .build()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        requests.add(req)
        return responses.removeFirst()
    }

    fun assertRequest(method: String, auth: String?, vararg params: Pair<String, Any?>): Request {
        val r = requests.removeFirst()
        r.url().toString() shouldBe expUrl
        r.method() shouldBe expMethod
        val expReq = ZabbixApiRequest(method, mapOf(*params), auth)
        getBody(r) shouldBe expReq.toJsonString()
        return r
    }

    fun getBody(r: Request): String {
        val b = Buffer()
        r.body()?.writeTo(b)
        return b.readUtf8()
    }

}