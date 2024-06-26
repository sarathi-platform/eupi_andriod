package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.repository.DeleteAllDataRepositoryImpl
import javax.inject.Inject

class DeleteAllGrantDataUseCase @Inject constructor(private val deleteAllDataRepositoryImpl: DeleteAllDataRepositoryImpl) {

    suspend operator fun invoke() {
        deleteAllDataRepositoryImpl.deleteAllDataFromDb()
    }
}