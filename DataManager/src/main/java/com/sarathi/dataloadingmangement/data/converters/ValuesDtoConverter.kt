package com.sarathi.dataloadingmangement.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nudge.core.BLANK_STRING
import com.sarathi.dataloadingmangement.model.survey.response.ValuesDto
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