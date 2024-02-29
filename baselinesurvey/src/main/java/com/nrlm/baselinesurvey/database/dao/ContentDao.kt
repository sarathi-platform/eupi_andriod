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
    fun insertContent(contents: List<ContentEntity>)

    @Query("SELECT * FROM $CONTENT_TABLE_NAME where contentKey=:contentkey")
    fun getContentFromIds(contentkey: String): ContentEntity

//    @Query("SELECT * FROM $CONTENT_TABLE_NAME where contentKey=:contentkey")
//    fun isContentFromIds(contentkey: String): Boolean

    @Query("DELETE FROM $CONTENT_TABLE_NAME")
    fun deleteContent()

//    @Query("Delete from $CONTENT_TABLE_NAME where surveyId = :surveyId and languageId = :languageId")
//    fun deleteContentFroLanguage(surveyId: Int, languageId: Int)

}