package com.gitlab.daring.fms.common.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper

inline fun <reified T> ObjectMapper.readList(content: String): List<T> {
    return readValue(content)
}

fun JsonNode.toMap(): Map<String, Any> {
    return JsonMapper.treeToValue(this)
}