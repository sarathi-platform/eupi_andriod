package com.nudge.core.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nudge.core.model.MetadataDto
import java.lang.reflect.Type

class MetadataDtoConverter {

    @TypeConverter
    fun fromMetadataDto(item: MetadataDto): String {
        val type: Type = object : TypeToken<MetadataDto?>() {}.type
        return Gson().toJson(item, type)
    }

    @TypeConverter
    fun toMetadataDto(metadataInString: String): MetadataDto {
        val type = object : TypeToken<MetadataDto?>() {}.type
        return Gson().fromJson(metadataInString, type)
    }

}