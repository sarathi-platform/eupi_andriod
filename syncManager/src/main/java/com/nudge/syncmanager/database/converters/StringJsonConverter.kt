package com.nudge.syncmanager.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class StringJsonConverter {

    @TypeConverter
    fun <T> fromQuestionOptions(item: T): String {
        val type: Type = object : TypeToken<List<T?>?>() {}.type
        return Gson().toJson(item, type)
    }

    @TypeConverter
    fun <T> toQuestionOptions(listInString: String): T {
        val type =
            object : TypeToken<List<T?>?>() {}.type
        return Gson().fromJson(listInString, type)
    }

}