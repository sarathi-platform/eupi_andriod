package com.sarathi.dataloadingmangement.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class IntConverter {
    @TypeConverter
    fun fromOptionValuesList(optionValues: List<Int?>?): String? {
        if (optionValues == null) {
            return null
        }
        val gson = Gson()
        val type = object :
            TypeToken<List<Int?>?>() {}.type
        return gson.toJson(optionValues, type)
    }

    @TypeConverter
    fun toOptionValuesList(optionValuesString: String?): List<Int>? {
        if (optionValuesString == null) {
            return null
        }
        val gson = Gson()
        val type = object :
            TypeToken<List<Int?>?>() {}.type
        return gson.fromJson<List<Int>>(optionValuesString, type)
    }
}