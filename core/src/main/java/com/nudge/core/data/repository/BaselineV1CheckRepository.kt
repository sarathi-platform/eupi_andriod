package com.nudge.core.data.repository

interface BaselineV1CheckRepository {

    fun getBaselineV1Ids(): String

    fun getStateId(): Int

}