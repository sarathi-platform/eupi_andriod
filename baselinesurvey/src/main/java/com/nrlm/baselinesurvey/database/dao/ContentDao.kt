package com.nrlm.baselinesurvey.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nrlm.baselinesurvey.CONTENT_TABLE_NAME
import com.nrlm.baselinesurvey.database.entity.ContentEntity

@Dao
interface ContentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContent(contentEntity: ContentEntity)

    @Query("SELECT * FROM $CONTENT_TABLE_NAME where sectionId=:sectionId  and surveyId=:surveyId and languageId=:languageId")
    fun getContentFromIds(surveyId: Int, sectionId: Int, languageId: Int): ContentEntity

    @Query("DELETE FROM $CONTENT_TABLE_NAME")
    fun deleteContent()

    @Query("Delete from $CONTENT_TABLE_NAME where surveyId = :surveyId and languageId = :languageId")
    fun deleteContentFroLanguage(surveyId: Int, languageId: Int)

}