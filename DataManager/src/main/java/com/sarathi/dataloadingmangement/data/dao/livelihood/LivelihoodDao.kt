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

    @Query("SELECT  COALESCE(lr.name, lt.name) AS name,lt.name as originalName,lt.status,lt.validations ,lt.type, lt.programLivelihoodId, lt.image from $LIVELIHOOD_TABLE_NAME lt left join $LIVELIHOOD_LANGUAGE_TABLE_NAME  lr On lt.programLivelihoodId = lr.referenceId And lr.userId= :userId And lr.languageCode = :languageCode and lr.referenceType = :referenceType  Where lt.userId =:userId  and lt.status = 1 ")
    fun getLivelihoodList(
        userId: String,
        languageCode: String,
        referenceType: String = LivelihoodLanguageReferenceType.Livelihood.name
    ): List<LivelihoodModel>

    @Query("SELECT  COALESCE(lr.name, lt.name) AS name,lt.name as originalName,lt.status, lt.validations,lt.type, lt.programLivelihoodId, lt.image from $LIVELIHOOD_TABLE_NAME lt left join $LIVELIHOOD_LANGUAGE_TABLE_NAME  lr On lt.programLivelihoodId = lr.referenceId And lr.userId= :userId And lr.languageCode = :languageCode and lr.referenceType = :referenceType Where lt.userId =:userId  and lt.status = 1  and lt.programLivelihoodId!=-1 ")
    fun getLivelihoodListWithoutNotDecided(
        userId: String,
        languageCode: String,
        referenceType: String = LivelihoodLanguageReferenceType.Livelihood.name
    ): List<LivelihoodModel>

    @Query("SELECT* from $LIVELIHOOD_TABLE_NAME where userId=:userId ")
    fun getLivelihoodForUser(
        userId: String,
    ): List<LivelihoodEntity>

    @Query("SELECT* from $LIVELIHOOD_TABLE_NAME where userId=:userId  and programLivelihoodId=:livelihoodId")
    fun getLivelihoodImageForUser(
        userId: String,
        livelihoodId: Int
    ): LivelihoodEntity

    @Query(
        "SELECT \n" +
                "    lt.name AS originalName,\n" +
                "    COALESCE(lr.name, lt.name) AS name,\n" +
                "    lt.status,\n" +
                "    lt.validations,\n" +
                "    lt.type,\n" +
                "    lt.programLivelihoodId\n" +
                "FROM \n" +
                "    livelihood_table lt\n" +
                "LEFT JOIN \n" +
                "    livelihood_language_reference_table lr \n" +
                "ON \n" +
                "    lt.programLivelihoodId = lr.referenceId \n" +
                "    AND lr.languageCode = :languageCode\n" +
                "    AND lr.userId = :userId\n" +
                "    AND lr.referenceType = :referenceType\n" +
                "WHERE \n" +
                "    lt.userId = :userId \n" +
                "    AND lt.status = 1\n" +
                "    AND lt.programLivelihoodId IN (:ids);"
    )
    fun getLivelihoodList(
        userId: String,
        languageCode: String,
        ids: List<Int>,
        referenceType: String = LivelihoodLanguageReferenceType.Livelihood.name
    ): List<LivelihoodModel>
}