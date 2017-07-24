package com.gitlab.daring.fms.common.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper

fun JsonNode.toMap(): Map<String, Any> {
    return JsonMapper.treeToValue(this)
}