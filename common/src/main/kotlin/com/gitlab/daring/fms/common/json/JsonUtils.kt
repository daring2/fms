package com.gitlab.daring.fms.common.json

import com.fasterxml.jackson.core.JsonParser.Feature.*
import com.fasterxml.jackson.core.JsonGenerator.Feature.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.*

object JsonUtils {

    val JsonMapper = createMapper()

    fun createMapper(): ObjectMapper {
        val m = ObjectMapper()
        m.configure(ALLOW_UNQUOTED_FIELD_NAMES, true)
        m.configure(ALLOW_SINGLE_QUOTES, true)
        m.configure(AUTO_CLOSE_TARGET, false)
        m.registerKotlinModule()
        return m
    }

}