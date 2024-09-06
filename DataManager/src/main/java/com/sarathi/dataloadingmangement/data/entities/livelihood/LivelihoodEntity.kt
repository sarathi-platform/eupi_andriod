package com.sarathi.dataloadingmangement.data.entities.livelihood

import androidx.room.Entity
import androidx.room.PrimaryKey
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
    var type: Int? = 0,
    var image: String?

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
                image = livelihood.image ?: BLANK_STRING
            )
        }

    }
}