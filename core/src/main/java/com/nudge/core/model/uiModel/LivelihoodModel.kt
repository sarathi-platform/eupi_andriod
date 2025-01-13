package com.nudge.core.model.uiModel

import androidx.room.TypeConverters
import com.nudge.core.BLANK_STRING
import com.nudge.core.database.converters.ValidationConverter
import com.nudge.core.model.response.Validations

data class LivelihoodModel(

    var name: String,
    var status: Int,
    var originalName: String,
    var type: String,
    @TypeConverters(ValidationConverter::class)
    val validations: List<Validations>?,
    var programLivelihoodId: Int,
    var image: String?
) {

    companion object {
        fun getAllFilter(): LivelihoodModel {
            return LivelihoodModel(
                programLivelihoodId = 0,
                name = "All",
                status = 1,
                originalName = "",
                validations = listOf(),
                type = BLANK_STRING,
                image = BLANK_STRING
            )
        }
    }

}