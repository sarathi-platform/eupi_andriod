package com.nudge.core

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap

object TabsCore {

    private val subTabIndex = mutableStateOf(0)
    private val tabIndex = mutableStateOf(0)

    private val tabToSubTabMap: SnapshotStateMap<Int, Int> = mutableStateMapOf()

    fun getSubTabIndex() = subTabIndex

    fun getTabIndex() = tabIndex

    fun setSubTabIndex(parentTabIndex: Int, subTabIndex: Int) {
//        subTabIndex.value = index
        tabToSubTabMap.put(parentTabIndex, subTabIndex)
    }

    fun setTabIndex(index: Int) {
        tabIndex.value = index
        if (!tabToSubTabMap.containsKey(index))
            tabToSubTabMap.put(index, 0)
    }

    fun getTabToSubTabMap(): Map<Int, Int> {
        return tabToSubTabMap
    }

    fun getSubTabForTabIndex(tabIndex: Int): Int {
        return tabToSubTabMap.get(tabIndex).value()
    }

}