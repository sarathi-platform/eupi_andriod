package com.nudge.core.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ListConvertor {

    @TypeConverter
    fun fromList(items: List<String>) : String {
        val type: Type = object : TypeToken<List<String>?>() {}.type
        return Gson().toJson(items, type)
    }

    @TypeConverter
    fun toList(listInString: String): List<String> {
        val type = object : TypeToken<List<String>?>() {}.type
        return Gson().fromJson(listInString, type)
    }

}