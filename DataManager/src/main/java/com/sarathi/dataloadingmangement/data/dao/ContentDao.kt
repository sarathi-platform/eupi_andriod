package com.sarathi.dataloadingmangement.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarathi.dataloadingmangement.CONTENT_TABLE_NAME
import com.sarathi.dataloadingmangement.data.entities.Content

@Dao
interface ContentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContent(contents: List<Content>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContent(contents: Content)

    @Query("SELECT * FROM $CONTENT_TABLE_NAME where contentKey=:contentkey and languageCode=:languageId")
    fun getContentFromIds(contentkey: String, languageId: String): Content

    @Query("SELECT contentValue FROM $CONTENT_TABLE_NAME where contentKey=:contentkey and languageCode=:languageId  and userId=:userId")
    fun getContentValue(contentkey: String, languageId: String, userId: String): String

    @Query("SELECT * FROM $CONTENT_TABLE_NAME ")
    fun getContentData(): List<Content>

    @Query("SELECT * FROM $CONTENT_TABLE_NAME where contentKey in (:contentKeys) and userId=:userId")
    fun getContentData(contentKeys: List<String>, userId: String): List<Content>

    @Query("SELECT * FROM $CONTENT_TABLE_NAME  where contentKey in (:contentKeys) and userId=:userId LIMIT :limit")
    fun getLimitedData(limit: Int, contentKeys: List<String>, userId: String): List<Content>

    @Query("SELECT count(*) FROM $CONTENT_TABLE_NAME where contentKey in (:contentKeys)  and userId=:userId")
    fun getContentCount(contentKeys: List<String>, userId: String): Int

    @Query("DELETE FROM $CONTENT_TABLE_NAME where userId=:userId")
    fun deleteContent(userId: String)

}