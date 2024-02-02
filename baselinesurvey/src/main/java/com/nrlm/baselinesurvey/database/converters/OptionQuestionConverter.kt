package com.nrlm.baselinesurvey.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nrlm.baselinesurvey.model.response.QuestionList
import java.lang.reflect.Type

class OptionQuestionConverter {
    @TypeConverter
    fun fromOptionQuestions(list: List<QuestionList>): String {
        val type: Type = object : TypeToken<List<QuestionList?>?>() {}.type
        return Gson().toJson(list, type)
    }

    @TypeConverter
    fun toOptionsQuestions(listInString: String): List<QuestionList> {
        val type =
            object : TypeToken<List<QuestionList?>?>() {}.type
        return Gson().fromJson(listInString, type)
    }
}