package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.util.CONTENT_TABLE_NAME

@Dao
interface ContentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContent(contents: List<Content>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContent(contents: Content)

    @Query("SELECT * FROM $CONTENT_TABLE_NAME where contentKey=:contentkey and languageCode=:languageId")
    fun getContentFromIds(contentkey: String, languageId: Int): Content

    @Query("SELECT * FROM $CONTENT_TABLE_NAME")
    fun getContentData(): List<Content>

    @Query("DELETE FROM $CONTENT_TABLE_NAME")
    fun deleteContent()

}