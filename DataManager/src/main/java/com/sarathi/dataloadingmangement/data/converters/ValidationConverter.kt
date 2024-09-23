package com.sarathi.dataloadingmangement.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sarathi.dataloadingmangement.model.response.Validation
import java.lang.reflect.Type

class ValidationConverter {

    @TypeConverter
    fun fromValidationDto(validation: Validation): String {

        val type: Type = object : TypeToken<Validation?>() {}.type
        return Gson().toJson(validation, type)
    }

    @TypeConverter
    fun toValidationDto(validation: String?): Validation? {

        val type =
            object : TypeToken<Validation?>() {}.type
        return Gson().fromJson(validation, type)
    }

}