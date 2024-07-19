package com.sarathi.dataloadingmangement.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nudge.core.BLANK_STRING
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import java.lang.reflect.Type


class ContentMapConverter {

    @TypeConverter
    fun fromContentMap(map: MutableList<MutableMap<Int?, List<ContentList?>?>>?): String {
        if (map?.isEmpty() == true)
            return BLANK_STRING
        val type: Type =
            object : TypeToken<MutableList<MutableMap<Int?, List<ContentList?>?>>>() {}.type
        return Gson().toJson(map, type)
    }

    @TypeConverter
    fun toContentMap(listInString: String?): MutableList<MutableMap<Int?, List<ContentList?>?>?> {
        if (listInString.isNullOrBlank() || listInString.equals("null", false))
            return mutableListOf()
        val type =
            object : TypeToken<MutableList<MutableMap<Int?, List<ContentList?>?>?>>() {}.type
        return Gson().fromJson(listInString, type)
    }
}