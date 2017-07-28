package com.gitlab.daring.fms.common.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.gitlab.daring.fms.common.util.CommonUtils.parseDuration
import java.time.Duration

object DurationJsonSupport {

    val handledType = Duration::class.java

    fun register(m: SimpleModule) {
        m.addSerializer(Serializer())
        m.addDeserializer(handledType, Deserializer())
    }

    class Serializer: StdSerializer<Duration>(handledType) {
        override fun serialize(v: Duration, gen: JsonGenerator, p: SerializerProvider) {
            gen.writeNumber(v.toMillis())
        }
    }

    class Deserializer: StdNodeBasedDeserializer<Duration>(handledType) {
        override fun convert(n: JsonNode, ctxt: DeserializationContext): Duration {
            return parseDuration(n.asText())
        }
    }

}