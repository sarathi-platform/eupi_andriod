package com.nudge.core

import androidx.compose.runtime.mutableStateOf

object TabsCore {

    private val subTabIndex = mutableStateOf(0)
    private val tabIndex = mutableStateOf(0)

    private val tabToSubTabMap: HashMap<Int, Int> = HashMap()

    fun getSubTabIndex() = subTabIndex

    fun getTabIndex() = tabIndex

    fun setSubTabIndex(index: Int) {
        subTabIndex.value = index
        tabToSubTabMap.put(getTabIndex().value, index)
    }

    fun setTabIndex(index: Int) {
        tabIndex.value = index
    }

    fun getTabToSubTabMap(): Map<Int, Int> {
        return tabToSubTabMap
    }

    fun getSubTabForTabIndex(tabIndex: Int): Int {
        return tabToSubTabMap.get(tabIndex).value()
    }

}