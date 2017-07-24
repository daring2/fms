package com.gitlab.daring.fms.common.json

import com.fasterxml.jackson.core.JsonParser.Feature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.*

object JsonUtils {

    val JsonMapper = createMapper()

    fun createMapper(): ObjectMapper {
        val m = ObjectMapper()
        m.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        m.configure(Feature.ALLOW_SINGLE_QUOTES, true)
        m.registerKotlinModule()
        return m
    }

}