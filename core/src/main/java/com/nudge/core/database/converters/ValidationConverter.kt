package com.nudge.core.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nudge.core.model.response.SurveyValidations
import com.nudge.core.model.response.Validations
import java.lang.reflect.Type

class ValidationConverter {

    @TypeConverter
    fun fromValidationDto(validation: List<Validations>?): String {

        val type: Type = object : TypeToken<List<Validations>?>() {}.type
        return Gson().toJson(validation, type)
    }

    @TypeConverter
    fun toValidationDto(validation: String?): List<Validations>? {
        val type = object : TypeToken<List<Validations>?>() {}.type
        return Gson().fromJson<List<Validations>>(validation, type)

    }

}

class SurveyValidationsConverter {

    @TypeConverter
    fun fromSurveyValidationsDto(validation: List<SurveyValidations>?): String {

        val type: Type = object : TypeToken<List<SurveyValidations>?>() {}.type
        return Gson().toJson(validation, type)
    }

    @TypeConverter
    fun toSurveyValidationsDto(validation: String?): List<SurveyValidations>? {
        val type = object : TypeToken<List<SurveyValidations>?>() {}.type
        return Gson().fromJson<List<SurveyValidations>>(validation, type)

    }

}