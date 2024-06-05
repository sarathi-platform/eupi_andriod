package com.sarathi.dataloadingmangement.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nudge.core.BLANK_STRING
import com.sarathi.dataloadingmangement.model.survey.response.ConditionsDto
import java.lang.reflect.Type

class ConditionsDtoConvertor {

    @TypeConverter
    fun fromOptionQuestions(list: List<ConditionsDto?>?): String {
        if (list?.isEmpty() == true)
            return BLANK_STRING
        val type: Type = object : TypeToken<List<ConditionsDto?>?>() {}.type
        return Gson().toJson(list, type)
    }

    @TypeConverter
    fun toOptionsQuestions(listInString: String?): List<ConditionsDto?>? {
        if (listInString == null || listInString.equals("null", false))
            return listOf()
        val type =
            object : TypeToken<List<ConditionsDto?>?>() {}.type
        return Gson().fromJson(listInString, type)
    }
}