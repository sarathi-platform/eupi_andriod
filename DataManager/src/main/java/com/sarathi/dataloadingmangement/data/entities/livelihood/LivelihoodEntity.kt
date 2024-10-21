package com.sarathi.dataloadingmangement.data.entities.livelihood

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nudge.core.database.converters.ValidationConverter
import com.nudge.core.model.response.Validations
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.LIVELIHOOD_TABLE_NAME
import com.sarathi.dataloadingmangement.model.response.Livelihood


@Entity(tableName = LIVELIHOOD_TABLE_NAME)
data class LivelihoodEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var livelihoodId: Int,
    var userId: String,
    var name: String,
    var status: Int,
    //Todo Add column type for migration
    var type: String? = BLANK_STRING,
    var image: String?,

    //Todo Add column in migration
    @TypeConverters(ValidationConverter::class)
    var validations: List<Validations>?

) {
    companion object {
        fun getLivelihoodEntity(
            userId: String,
            livelihood: Livelihood
        ): LivelihoodEntity {

            return LivelihoodEntity(
                id = 0,
                livelihoodId = livelihood.id ?: 0,
                userId = userId,
                name = livelihood.name ?: BLANK_STRING,
                status = livelihood.status ?: 0,
                image = livelihood.image ?: BLANK_STRING,
                validations = livelihood.validations,
                type = livelihood.type ?: BLANK_STRING
            )
        }

    }
}