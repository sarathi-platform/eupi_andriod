package com.sarathi.contentmodule.content_downloder.domain.repository

import com.sarathi.dataloadingmangement.data.entities.Content

interface IContentDownloader {
    suspend fun getContentDataFromDb(): List<Content>

}