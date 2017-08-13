package com.gitlab.daring.fms.common.config

import com.fasterxml.jackson.module.kotlin.convertValue
import com.gitlab.daring.fms.common.json.JsonUtils.JsonMapper
import com.google.common.net.HostAndPort
import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigUtil.splitPath

fun Config.getMillis(path: String): Long {
    return getDuration(path).toMillis()
}

fun Config.getHostAndPort(path: String): HostAndPort {
    return HostAndPort.fromString(getString(path))
}

fun Config.toMap(): Map<String, Any> {
    return entrySet().map { ce ->
        val key = splitPath(ce.key).joinToString(".")
        key to ce.value.unwrapped()
    }.toMap()
}

inline fun <reified T: Any> Config.toBean(): T {
    return JsonMapper.convertValue(toMap())
}

inline fun <reified T: Any> Config.getBean(path: String): T {
    return getConfig(path).toBean()
}

fun <T> Config.getOpt(f: Config.() -> T): T? {
    return try {
        f(this)
    } catch (e: ConfigException.Missing) {
        return null
    }
}