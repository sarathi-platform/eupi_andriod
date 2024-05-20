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

    @Query("SELECT * FROM $CONTENT_TABLE_NAME where contentKey=:contentkey and languageCode=:languageId")
    fun getContentFromIds(contentkey: String, languageId: Int): ContentEntity


    @Query("DELETE FROM $CONTENT_TABLE_NAME")
    fun deleteContent()

}