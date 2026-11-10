package com.joker.coolmall.core.model.entity

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement

object ConditionSerializer : KSerializer<Condition?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Condition", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Condition?) {
        if (value == null) {
            encoder.encodeNull()
        } else {
            encoder.encodeSerializableValue(Condition.serializer(), value)
        }
    }

    override fun deserialize(decoder: Decoder): Condition? {
        return try {
            when (decoder) {
                is JsonDecoder -> {
                    val element = decoder.decodeJsonElement()
                    when {
                        element is JsonPrimitive && element.isString -> {
                            val jsonString = element.content
                            if (jsonString.isBlank()) null
                            else Json.decodeFromString(Condition.serializer(), jsonString)
                        }
                        else -> Json.decodeFromJsonElement(Condition.serializer(), element)
                    }
                }
                else -> decoder.decodeSerializableValue(Condition.serializer())
            }
        } catch (_: Exception) {
            null
        }
    }
}

@Serializable
data class Coupon(
    val id: Long = 0,
    val title: String = "",
    val description: String = "",
    val type: Int = 0,
    val amount: Double = 0.0,
    val num: Int = 0,
    val receivedNum: Int = 0,
    val startTime: String? = null,
    val endTime: String? = null,
    val status: Int = 0,
    @Serializable(with = ConditionSerializer::class)
    val condition: Condition? = null,
    val createTime: String? = null,
    val updateTime: String? = null,
    val useStatus: Int? = null,
)
