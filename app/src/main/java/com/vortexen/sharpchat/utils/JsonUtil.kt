package com.vortexen.sharpchat.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement

object JsonUtil {
    /**
     * Converts a serializable data class to a JsonObject.
     * @param T The type of the data class.
     * @param data The data class instance.
     * @return A JsonObject representation of the data class.
     * @throws IllegalArgumentException if the result is not a JsonObject.
     */
    inline fun <reified T> toJsonObject(data: T): JsonObject {
        val jsonElement = Json.encodeToJsonElement(data)
        return jsonElement as? JsonObject
            ?: throw IllegalArgumentException("Data could not be converted to JsonObject")
    }

}
