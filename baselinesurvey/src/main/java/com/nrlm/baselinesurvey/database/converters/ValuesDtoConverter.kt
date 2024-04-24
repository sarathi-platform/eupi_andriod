package com.nrlm.baselinesurvey.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.model.datamodel.ValuesDto
import java.lang.reflect.Type

class ValuesDtoConverter {

    @TypeConverter
    fun fromOValuesDto(list: List<ValuesDto?>?): String {
        if (list?.isEmpty() == true)
            return BLANK_STRING
        val type: Type = object : TypeToken<List<ValuesDto?>?>() {}.type
        return Gson().toJson(list, type)
    }

    @TypeConverter
    fun toValuesDto(listInString: String?): List<ValuesDto?>? {
        if (listInString == null || listInString.equals("null", false))
            return listOf()
        val type =
            object : TypeToken<List<ValuesDto?>?>() {}.type
        return Gson().fromJson(listInString, type)
    }

}