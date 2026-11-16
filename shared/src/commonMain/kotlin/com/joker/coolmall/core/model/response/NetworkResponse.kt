package com.joker.coolmallkmp.core.model.response

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive

object FlexibleIntSerializer : KSerializer<Int> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("FlexibleInt", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Int {
        if (decoder is JsonDecoder) {
            val primitive = decoder.decodeJsonElement() as? JsonPrimitive
                ?: throw SerializationException("Expected a number or numeric string")
            return primitive.content.toIntOrNull()
                ?: throw SerializationException("Invalid integer: ${primitive.content}")
        }
        return decoder.decodeInt()
    }

    override fun serialize(encoder: Encoder, value: Int) {
        encoder.encodeInt(value)
    }
}

/**
 * 解析网络响应
 *
 * @param T 数据类型
 * @param data 真实数据
 * @param code 状态码 等于1000表示成功
 * @param message 出错的提示信息
 */
@Serializable
data class NetworkResponse<T>(
    val data: T? = null,
    @Serializable(with = FlexibleIntSerializer::class)
    val code: Int = 1000,
    val message: String? = null,
) {
    val isSucceeded: Boolean
        get() = code == 1000
}
