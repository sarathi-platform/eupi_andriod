package com.nudge.core

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap

object FilterCore {

    private const val DEFAULT_FILTER_INDEX_VALUE = 0

    private val activityFilterMap: SnapshotStateMap<Int, Int> = mutableStateMapOf()

    fun getFilterValueForActivity(activityId: Int): Int {
        return if (activityFilterMap.containsKey(activityId))
            activityFilterMap[activityId].value(DEFAULT_FILTER_INDEX_VALUE)
        else {
            activityFilterMap[activityId] = DEFAULT_FILTER_INDEX_VALUE
            activityFilterMap[activityId].value(DEFAULT_FILTER_INDEX_VALUE)
        }
    }

    fun setFilterValueForActivity(activityId: Int, selectedFilterIndex: Int) {
        activityFilterMap[activityId] = selectedFilterIndex
    }

}