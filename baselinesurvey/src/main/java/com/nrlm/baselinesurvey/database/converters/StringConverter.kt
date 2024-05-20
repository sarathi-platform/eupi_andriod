package com.nrlm.baselinesurvey.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nrlm.baselinesurvey.BLANK_STRING

//TODO change back to string converter and use different converter for ValuesDto
class StringConverter {
    @TypeConverter
    fun fromOptionValuesList(optionValues: List<String?>?): String? {
        if (optionValues == null) {
            return BLANK_STRING
        }
        val gson = Gson()
        val type = object :
            TypeToken<List<String?>?>() {}.type
        return gson.toJson(optionValues, type)
    }

    @TypeConverter
    fun toOptionValuesList(optionValuesString: String?): List<String>? {
        if (optionValuesString == null || optionValuesString.equals("null", false)) {
            return listOf()
        }
        val gson = Gson()
        val type = object :
            TypeToken<List<String?>?>() {}.type
        return gson.fromJson<List<String>>(optionValuesString, type)
    }
}