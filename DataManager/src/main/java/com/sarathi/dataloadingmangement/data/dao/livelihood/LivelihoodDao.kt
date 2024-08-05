package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.LIVELIHOOD_TABLE_NAME
import com.sarathi.dataloadingmangement.LIVILIHOOD_LANGUAGE_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodEntity
import com.sarathi.dataloadingmangement.model.uiModel.livelihood.LivelihoodDropDownUiModel


@Dao
interface LivelihoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLivelihood(livelihood: LivelihoodEntity)

    @Query("DELETE FROM $LIVELIHOOD_TABLE_NAME where userId=:userId ")
    fun deleteLivelihoodForUser(userId: String)

    @Query ("SELECT lt.id,lr.name,lt.status from $LIVELIHOOD_TABLE_NAME lt join $LIVILIHOOD_LANGUAGE_TABLE_NAME  lr On lt.id = lr.id Where lt.userId =:userId And lr.userId= :userId And lr.languageCode = :languageCode")
    fun  getAssetsData(userId: String,languageCode:String):List<LivelihoodDropDownUiModel>
}