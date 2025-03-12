package com.nudge.core.data.repository

interface FetchRemoteQueryFromNetworkRepository {

    suspend fun fetchRemoteQueryFromNetwork()

    suspend fun saveRemoteQueryToDb()

}