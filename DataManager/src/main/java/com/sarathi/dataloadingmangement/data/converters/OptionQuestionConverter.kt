package com.sarathi.dataloadingmangement.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.model.survey.response.QuestionList
import java.lang.reflect.Type

class OptionQuestionConverter {
    @TypeConverter
    fun fromOptionQuestions(list: List<QuestionList?>?): String {
        if (list?.isEmpty() == true)
            return BLANK_STRING
        val type: Type = object : TypeToken<List<QuestionList?>?>() {}.type
        return Gson().toJson(list, type)
    }

    @TypeConverter
    fun toOptionsQuestions(listInString: String?): List<QuestionList?>? {
        if (listInString == null || listInString.equals("null", false))
            return listOf()
        val type =
            object : TypeToken<List<QuestionList?>?>() {}.type
        return Gson().fromJson(listInString, type)
    }
}