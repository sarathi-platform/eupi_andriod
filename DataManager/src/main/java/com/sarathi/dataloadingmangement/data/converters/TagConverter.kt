package com.sarathi.dataloadingmangement.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TagConverter {
    @TypeConverter
    fun fromIntListToJson(programLivelihoodReferenceId: List<Int>?): String? {
        if (programLivelihoodReferenceId == null) {
            return null
        }
        val gson = Gson()
        val type = object :
            TypeToken<List<Int?>?>() {}.type
        return gson.toJson(programLivelihoodReferenceId, type)
    }

    @TypeConverter
    fun fromJsonToIntList(programLivelihoodReferenceId: String?): List<Int>? {
        if (programLivelihoodReferenceId == null) {
            return null
        }
        val gson = Gson()
        val type = object :
            TypeToken<List<Int?>?>() {}.type
        return gson.fromJson<List<Int>>(programLivelihoodReferenceId, type)
    }
}