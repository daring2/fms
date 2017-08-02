package com.gitlab.daring.fms.zabbix.util

import com.fasterxml.jackson.module.kotlin.readValue
import com.gitlab.daring.fms.common.json.JsonUtils
import java.nio.charset.Charset

object ZabbixProtocolUtils {

    val HeaderSize = 13

    val ZbxNotSupported = "ZBX_NOTSUPPORTED\u0000"
    val ZbxError = "ZBX_ERROR"

    fun parseResponse(bs: ByteArray, charset: Charset): String {
        if (bs.size < HeaderSize)
            throw RuntimeException("invalid response")
        return String(bs, HeaderSize, bs.size - HeaderSize, charset)
    }

    inline fun <reified T: Any> parseJsonResponse(bs: ByteArray): T {
        val str = parseResponse(bs, Charsets.UTF_8)
        return JsonUtils.JsonMapper.readValue(str)
    }
    
}