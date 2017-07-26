package com.gitlab.daring.fms.common.config

import com.fasterxml.jackson.module.kotlin.convertValue
import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import com.typesafe.config.Config
import com.typesafe.config.ConfigUtil.splitPath

fun Config.getMillis(path: String): Long {
    return getDuration(path).toMillis()
}

fun Config.toMap(): Map<String, Any> {
    return entrySet().map { ce ->
        val key = splitPath(ce.key).joinToString(".")
        key to ce.value.unwrapped()
    }.toMap()
}

inline fun <reified T: Any> Config.getBean(path: String): T {
    val m = getConfig(path).toMap()
    return JsonMapper.convertValue(m)
}