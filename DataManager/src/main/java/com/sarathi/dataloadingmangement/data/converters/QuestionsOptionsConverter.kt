package com.sarathi.dataloadingmangement.data.converters

import android.text.TextUtils
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sarathi.dataloadingmangement.model.uiModel.OptionsUiModel
import java.lang.reflect.Type


class QuestionsOptionsConverter {

    @TypeConverter
    fun fromQuestionOptions(list: List<OptionsUiModel>): String {
        val type: Type = object : TypeToken<List<OptionsUiModel?>?>() {}.type
        return Gson().toJson(list, type)
    }

    @TypeConverter
    fun toQuestionOptions(listInString: String?): List<OptionsUiModel> {
        if (TextUtils.isEmpty(listInString)) {
            return emptyList()
        }
        val type = object : TypeToken<List<OptionsUiModel?>?>() {}.type
        return Gson().fromJson(listInString, type)
    }
}
