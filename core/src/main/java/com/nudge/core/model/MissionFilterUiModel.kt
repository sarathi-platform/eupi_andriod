package com.nudge.core.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonIOException
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.nudge.core.BLANK_STRING
import com.nudge.core.utils.CoreLogger
import java.lang.reflect.Type

data class FilterUiModel(
    val type: FilterType,
    val filterValue: String,
    val filterLabel: String,
    val imageFileName: String?
) {

    companion object {

        fun getAllFilter(
            filterValue: String,
            filterLabel: String,
            imageFileName: String?
        ): FilterUiModel {
            return FilterUiModel(
                type = FilterType.ALL,
                filterValue = filterValue,
                filterLabel = filterLabel,
                imageFileName = imageFileName
            )
        }

        fun getGeneralFilter(
            filterValue: String,
            filterLabel: String,
            imageFileName: String?
        ): FilterUiModel {
            return FilterUiModel(
                type = FilterType.GENERAL,
                filterValue = filterValue,
                filterLabel = filterLabel,
                imageFileName = imageFileName
            )
        }

    }

}

sealed class FilterType {
    object ALL : FilterType()
    object GENERAL : FilterType()
    data class OTHER(val filterValue: Any) : FilterType()
}

class FilterTypeAdapter : JsonDeserializer<FilterType>, JsonSerializer<FilterType> {
    override fun serialize(
        src: FilterType?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return when (src) {
            is FilterType.ALL -> JsonObject().apply { addProperty("type", "ALL") }
            is FilterType.GENERAL -> JsonObject().apply { addProperty("type", "GENERAL") }
            is FilterType.OTHER -> JsonObject().apply {
                addProperty("type", "OTHER")
                add("filterValue", context?.serialize(src.filterValue))
            }

            else -> throw JsonIOException("Unknown FilterType: $src")
        }
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): FilterType {
        try {
            val jsonObject =
                json?.asJsonObject ?: throw JsonIOException("Invalid JSON for FilterType")
            val type = jsonObject.get("type").asString
            return when (type) {
                "ALL" -> FilterType.ALL
                "GENERAL" -> FilterType.GENERAL
                "OTHER" -> {
                    val filterValue = jsonObject.get("filterValue")
                    FilterType.OTHER(
                        context?.deserialize(filterValue, Int::class.java) ?: BLANK_STRING
                    )
                }

                else -> throw JsonIOException("Unknown FilterType type: $type")
            }
        } catch (ex: Exception) {
            CoreLogger.e(
                tag = "FilterTypeAdapter",
                msg = "deserialize -> ${ex.message}",
                ex = ex,
                stackTrace = true
            )
            return FilterType.GENERAL
        }

    }
}
