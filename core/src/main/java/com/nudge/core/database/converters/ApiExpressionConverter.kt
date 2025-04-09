package com.nudge.core.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nudge.core.database.entities.api.ApiExpression
import java.lang.reflect.Type

class ApiExpressionConverter {

    @TypeConverter
    fun fromApiExpressionConverter(expression: List<ApiExpression>?): String {
        val type: Type = object : TypeToken<List<ApiExpression>?>() {}.type
        return Gson().toJson(expression, type)
    }

    @TypeConverter
    fun toApiExpressionConverter(expression: String?): List<ApiExpression>? {
        val type = object : TypeToken<List<ApiExpression>?>() {}.type
        return Gson().fromJson<List<ApiExpression>>(expression, type)

    }
}