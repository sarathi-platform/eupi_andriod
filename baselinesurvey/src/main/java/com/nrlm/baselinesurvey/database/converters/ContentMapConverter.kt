package com.nrlm.baselinesurvey.database.converters

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.nrlm.baselinesurvey.BLANK_STRING
import com.nrlm.baselinesurvey.model.response.ContentList
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