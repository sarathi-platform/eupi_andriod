package com.nudge.core.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class StringJsonConverter {

    @TypeConverter
    fun <Any> fromQuestionOptions(item: Any): String {
        val type: Type = object : TypeToken<kotlin.Any?>() {}.type
        return Gson().toJson(item, type)
    }

    @TypeConverter
    fun <Any> toQuestionOptions(listInString: String): Any {
        val type = object : TypeToken<Any?>() {}.type
        return Gson().fromJson(listInString, type)
    }

}