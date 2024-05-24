package com.sarathi.contentmodule.ui.content_screen.domain.usecase

import com.sarathi.contentmodule.content_downloder.domain.repository.IContentDownloader
import com.sarathi.dataloadingmangement.data.entities.Content

class FetchContentUseCase(private val repository: IContentDownloader) {
    suspend fun getContentData(): List<Content> {
        return repository.getContentDataFromDb()
    }
}