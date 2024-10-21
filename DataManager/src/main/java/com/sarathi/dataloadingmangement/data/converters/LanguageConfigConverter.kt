package com.sarathi.dataloadingmangement.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nudge.core.BLANK_STRING
import com.sarathi.dataloadingmangement.network.response.LanguageModel
import java.lang.reflect.Type

class LanguageConfigConverter {
    @TypeConverter
    fun fromLanguageConfig(list: List<LanguageModel?>?): String {
        if (list?.isEmpty() == true)
            return BLANK_STRING
        val type: Type = object : TypeToken<List<LanguageModel?>?>() {}.type
        return Gson().toJson(list, type)
    }

    @TypeConverter
    fun toLanguageConfig(listInString: String?): List<LanguageModel?>? {
        if (listInString.isNullOrEmpty() || listInString.equals("null", true))
            return listOf()
        val type =
            object : TypeToken<List<LanguageModel?>?>() {}.type
        return Gson().fromJson(listInString, type)
    }
}