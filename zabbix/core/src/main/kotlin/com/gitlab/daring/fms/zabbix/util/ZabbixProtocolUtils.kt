package com.gitlab.daring.fms.zabbix.util

import com.fasterxml.jackson.module.kotlin.readValue
import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder.LITTLE_ENDIAN
import java.nio.charset.Charset

internal object ZabbixProtocolUtils {

    val HeaderFixedBytes = "ZBXD\u0001".toByteArray()
    val HeaderSize = 13

    val ZbxNotSupported = "ZBX_NOTSUPPORTED\u0000"
    val ZbxError = "ZBX_ERROR"

    fun parseResponse(bs: ByteArray, charset: Charset): String {
        if (bs.size < HeaderSize)
            throw RuntimeException("invalid response")
        return String(bs, HeaderSize, bs.size - HeaderSize, charset)
    }

    inline fun <reified T: Any> parseJsonResponse(input: InputStream): T {
        input.skip(HeaderSize.toLong())
        return JsonMapper.readValue<T>(input)
    }

    fun buildMessage(data: String): ByteArray {
        val bs = data.toByteArray()
        val buffer = ByteBuffer.allocate(HeaderSize + bs.size).order(LITTLE_ENDIAN)
        buffer.put(HeaderFixedBytes).putLong(bs.size.toLong()).put(bs)
        return buffer.array()
    }
    
}