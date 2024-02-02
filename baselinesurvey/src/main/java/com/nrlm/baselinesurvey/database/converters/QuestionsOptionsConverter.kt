package com.nrlm.baselinesurvey.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nrlm.baselinesurvey.database.entity.OptionItemEntity
import com.nrlm.baselinesurvey.model.datamodel.OptionsItem
import java.lang.reflect.Type


class QuestionsOptionsConverter {

    @TypeConverter
    fun fromQuestionOptions(list: List<OptionItemEntity>): String {
        val type: Type = object : TypeToken<List<OptionItemEntity?>?>() {}.type
        return Gson().toJson(list, type)
    }

    @TypeConverter
    fun toQuestionOptions(listInString: String): List<OptionItemEntity> {
        val type =
            object : TypeToken<List<OptionsItem?>?>() {}.type
        return Gson().fromJson(listInString, type)
    }
 }
