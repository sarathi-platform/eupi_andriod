package com.sarathi.dataloadingmangement.data.dao.livelihood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nudge.core.model.uiModel.LivelihoodModel
import com.sarathi.dataloadingmangement.LIVELIHOOD_LANGUAGE_TABLE_NAME
import com.sarathi.dataloadingmangement.LIVELIHOOD_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.livelihood.LivelihoodEntity
import com.sarathi.dataloadingmangement.enums.LivelihoodLanguageReferenceType


@Dao
interface LivelihoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLivelihood(livelihood: LivelihoodEntity)

    @Query("DELETE FROM $LIVELIHOOD_TABLE_NAME where userId=:userId ")
    fun deleteLivelihoodForUser(userId: String)

    @Query("SELECT lt.livelihoodId,lr.name,lt.name as originalName,lt.status from $LIVELIHOOD_TABLE_NAME lt join $LIVELIHOOD_LANGUAGE_TABLE_NAME  lr On lt.livelihoodId = lr.livelihoodId Where lt.userId =:userId And lr.userId= :userId And lr.languageCode = :languageCode and lt.status = 1 and lr.referenceType = :referenceType ")
    fun getLivelihoodList(
        userId: String,
        languageCode: String,
        referenceType: String = LivelihoodLanguageReferenceType.Livelihood.name
    ): List<LivelihoodModel>

    @Query("SELECT lt.livelihoodId,lr.name,lt.name as originalName,lt.status from $LIVELIHOOD_TABLE_NAME lt join $LIVELIHOOD_LANGUAGE_TABLE_NAME  lr On lt.livelihoodId = lr.livelihoodId Where lt.userId =:userId And lr.userId= :userId And lr.languageCode = :languageCode and lt.status = 1 and lr.referenceType = :referenceType and lt.livelihoodId!=-1 ")
    fun getLivelihoodListWithoutNotDecided(
        userId: String,
        languageCode: String,
        referenceType: String = LivelihoodLanguageReferenceType.Livelihood.name
    ): List<LivelihoodModel>

    @Query("SELECT* from $LIVELIHOOD_TABLE_NAME where userId=:userId ")
    fun getLivelihoodForUser(
        userId: String,
    ): List<LivelihoodEntity>

    @Query("SELECT* from $LIVELIHOOD_TABLE_NAME where userId=:userId  and livelihoodId=:livelihoodId")
    fun getLivelihoodImageForUser(
        userId: String,
        livelihoodId: Int
    ): LivelihoodEntity

    @Query("SELECT lt.livelihoodId,lr.name,lt.name as originalName,lt.status from $LIVELIHOOD_TABLE_NAME lt join $LIVELIHOOD_LANGUAGE_TABLE_NAME  lr On lt.livelihoodId = lr.livelihoodId Where lt.userId =:userId And lr.userId= :userId And lr.languageCode = :languageCode and lt.status = 1 and lr.referenceType = :referenceType and lt.livelihoodId in(:ids)")
    fun getLivelihoodList(
        userId: String,
        languageCode: String,
        ids: List<Int>,
        referenceType: String = LivelihoodLanguageReferenceType.Livelihood.name
    ): List<LivelihoodModel>
}