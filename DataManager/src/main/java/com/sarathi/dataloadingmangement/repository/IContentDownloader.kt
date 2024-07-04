package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.Content

interface IContentDownloader {
    suspend fun getContentDataFromDb(): List<Content>
    suspend fun getSpecificContentDataFromDb(contentKeys: List<String>): List<Content>

    suspend fun getContentValue(contentKey: String): String
    suspend fun getLimitedContentData(limit: Int, contentKeys: List<String>): List<Content>
    suspend fun getContentCount(contentKeys: List<String>): Int
    fun getContentKeyFromContentConfig(referenceID: Int, referenceType: Int): List<String>
    suspend fun getDidiImagesUrl(): List<String>

    suspend fun getDidiImageUrlForSmallGroup(): List<String>

}