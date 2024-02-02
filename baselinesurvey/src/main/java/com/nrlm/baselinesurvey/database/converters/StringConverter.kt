package com.nrlm.baselinesurvey.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StringConverter {
    @TypeConverter
    fun fromOptionValuesList(optionValues: List<String?>?): String? {
        if (optionValues == null) {
            return null
        }
        val gson = Gson()
        val type = object :
            TypeToken<List<String?>?>() {}.type
        return gson.toJson(optionValues, type)
    }

    @TypeConverter
    fun toOptionValuesList(optionValuesString: String?): List<String>? {
        if (optionValuesString == null) {
            return null
        }
        val gson = Gson()
        val type = object :
            TypeToken<List<String?>?>() {}.type
        return gson.fromJson<List<String>>(optionValuesString, type)
    }
}