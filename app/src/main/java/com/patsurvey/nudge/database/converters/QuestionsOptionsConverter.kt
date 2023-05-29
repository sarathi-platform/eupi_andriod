package com.patsurvey.nudge.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.patsurvey.nudge.model.response.OptionsItem
import java.lang.reflect.Type


class QuestionsOptionsConverter {

    @TypeConverter
    fun fromQuestionOptions(list: List<OptionsItem>): String {
        val type: Type = object : TypeToken<List<OptionsItem?>?>() {}.type
        return Gson().toJson(list, type)
    }

    @TypeConverter
    fun toQuestionOptions(listInString: String): List<OptionsItem> {
        val type =
            object : TypeToken<List<OptionsItem?>?>() {}.type
        return Gson().fromJson(listInString, type)
    }
 }
