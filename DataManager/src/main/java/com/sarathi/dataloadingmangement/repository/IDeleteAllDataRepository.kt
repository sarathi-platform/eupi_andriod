package com.sarathi.dataloadingmangement.repository

interface IDeleteAllDataRepository {

    suspend fun deleteAllDataFromDb()
}