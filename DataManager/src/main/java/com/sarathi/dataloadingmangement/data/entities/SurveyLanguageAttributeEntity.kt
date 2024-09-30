package com.sarathi.dataloadingmangement.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.SURVEY_LANGUAGE_ATTRIBUTE_TABLE_NAME
import com.sarathi.dataloadingmangement.model.survey.response.SurveyLanguageAttributes


@Entity(tableName = SURVEY_LANGUAGE_ATTRIBUTE_TABLE_NAME)
data class SurveyLanguageAttributeEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int = 0,
    var userId: String? = BLANK_STRING,
    var paraphrase: String,
    var description: String,
    var languageCode: String,
    var referenceId: Int,
    var referenceType: String

) {
    companion object {
        fun getSurveyLanguageAttributeEntity(
            userId: String,
            languageAttributes: SurveyLanguageAttributes,
            referenceId: Int,
            type: String
        ): SurveyLanguageAttributeEntity {
            return SurveyLanguageAttributeEntity(
                id = 0,
                userId = userId,
                paraphrase = languageAttributes.paraphrase ?: BLANK_STRING,
                description = languageAttributes.description,
                languageCode = languageAttributes.languageCode,
                referenceId = referenceId,
                referenceType = type

            )

        }


    }
}
