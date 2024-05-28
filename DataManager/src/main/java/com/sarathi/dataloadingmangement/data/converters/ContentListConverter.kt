package com.sarathi.dataloadingmangement.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nudge.core.BLANK_STRING
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import java.lang.reflect.Type

class ContentListConverter {
    @TypeConverter
    fun fromContents(list: List<ContentList?>?): String {
        if (list?.isEmpty() == true)
            return BLANK_STRING
        val type: Type = object : TypeToken<List<ContentList?>?>() {}.type
        return Gson().toJson(list, type)
    }

    @TypeConverter
    fun toContents(listInString: String?): List<ContentList?>? {
        if (listInString.isNullOrEmpty() || listInString.equals("null", true))
            return listOf()
        val type =
            object : TypeToken<List<ContentList?>?>() {}.type
        return Gson().fromJson(listInString, type)
    }
}