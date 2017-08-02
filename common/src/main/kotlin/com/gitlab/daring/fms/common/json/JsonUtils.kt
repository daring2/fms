package com.gitlab.daring.fms.common.json

import com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_TARGET
import com.fasterxml.jackson.core.JsonParser.Feature.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object JsonUtils {

    val JsonMapper = createMapper()

    fun createMapper(): ObjectMapper {
        val m = ObjectMapper()
        m.configure(ALLOW_UNQUOTED_FIELD_NAMES, true)
        m.configure(ALLOW_SINGLE_QUOTES, true)
        m.configure(AUTO_CLOSE_SOURCE, false)
        m.configure(AUTO_CLOSE_TARGET, false)
        m.registerKotlinModule()
        m.registerModule(createExtModule())
        return m
    }

    fun createExtModule(): SimpleModule {
        val m = SimpleModule("fmsModule")
        DurationJsonSupport.register(m)
        return m
    }

}