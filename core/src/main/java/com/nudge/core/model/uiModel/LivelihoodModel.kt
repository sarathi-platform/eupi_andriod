package com.nudge.core.model.uiModel

import androidx.room.TypeConverters
import com.nudge.core.BLANK_STRING
import com.nudge.core.database.converters.ValidationConverter
import com.nudge.core.model.response.Validations

data class LivelihoodModel(
    var livelihoodId: Int,
    var name: String,
    var status: Int,
    var originalName: String,
    var type: String,
    @TypeConverters(ValidationConverter::class)
    val validations: List<Validations>?
) {

    companion object {
        fun getAllFilter(): LivelihoodModel {
            return LivelihoodModel(
                livelihoodId = 0,
                "All",
                status = 1,
                originalName = "",
                validations = listOf(),
                type = BLANK_STRING
            )
        }
    }

}