package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodEventEntity
import com.sarathi.dataloadingmangement.model.uiModel.incomeExpense.LivelihoodEventUiModel


@Dao
interface LivelihoodEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLivelihoodEvent(livelihoodEventEntity: LivelihoodEventEntity)

    @Query(
        "select livelihood_event_table.eventId as id,livelihood_event_table.type as eventType, COALESCE(livelihood_language_reference_table.name, livelihood_event_table.name) AS name, livelihood_event_table.livelihoodId as livelihoodId, livelihood_event_table.name as originalName\n" +
                " from livelihood_event_table left join livelihood_language_reference_table \n" +
                " on livelihood_event_table.eventId= livelihood_language_reference_table.referenceId and \n" +
                " livelihood_language_reference_table.referenceType=:referenceType and" +
                " livelihood_language_reference_table.userId=:userId and" +
                " livelihood_language_reference_table.languageCode=:languageCode " +
                " where \n" +
                " livelihood_event_table.userId=:userId  and" +
                " livelihood_event_table.livelihoodId=:livelihoodId group by livelihood_event_table.eventId "
    )
    fun getEventsForLivelihood(
        livelihoodId: Int,
        userId: String,
        referenceType: String = "Event",
        languageCode: String,
    ): List<LivelihoodEventUiModel>

    @Query(
        "select livelihood_event_table.eventId as id,livelihood_event_table.type as eventType,COALESCE(livelihood_language_reference_table.name, livelihood_event_table.name) AS name, livelihood_event_table.livelihoodId as livelihoodId,livelihood_event_table.name as originalName \n" +
                " from livelihood_event_table left join livelihood_language_reference_table \n" +
                " on livelihood_event_table.eventId= livelihood_language_reference_table.referenceId and \n" +
                " livelihood_language_reference_table.languageCode=:languageCode and\n" +
                " livelihood_language_reference_table.referenceType=:referenceType and" +
                " livelihood_language_reference_table.userId=:userId " +
                " where " +
                " livelihood_event_table.userId=:userId  and" +
                " livelihood_event_table.livelihoodId in (:livelihoodIds) "
    )
    fun getEventsForLivelihood(
        livelihoodIds: List<Int>,
        userId: String,
        referenceType: String = "Event",
        languageCode: String,
    ): List<LivelihoodEventUiModel>

    @Query("DELETE from livelihood_event_table where userId = :userId")
    fun deleteLivelihoodEventForUser(userId: String)

}