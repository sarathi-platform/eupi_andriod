package com.nudge.core.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nudge.core.model.response.Validations
import java.lang.reflect.Type

class ValidationConverter {

    @TypeConverter
    fun fromValidationDto(validation: List<Validations>): String {

        val type: Type = object : TypeToken<List<Validations>?>() {}.type
        return Gson().toJson(validation, type)
    }

    @TypeConverter
    fun toValidationDto(validation: String?): List<Validations>? {
        val type = object : TypeToken<List<Validations>?>() {}.type
        return Gson().fromJson(validation, type)
    }

}