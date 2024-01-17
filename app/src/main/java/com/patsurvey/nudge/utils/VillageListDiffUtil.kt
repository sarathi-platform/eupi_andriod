package com.patsurvey.nudge.utils

import androidx.recyclerview.widget.DiffUtil
import com.patsurvey.nudge.database.VillageEntity

class VillageListDiffUtil(
    private val defaultLanguageVillageList: List<VillageEntity>,
    private val localLanguageVillageList: List<VillageEntity>,
): DiffUtil.Callback() {

    val areListsSame = defaultLanguageVillageList.size == localLanguageVillageList.size

    override fun getOldListSize(): Int {
        return defaultLanguageVillageList.size
    }

    override fun getNewListSize(): Int {
        return localLanguageVillageList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return defaultLanguageVillageList[oldItemPosition].id == localLanguageVillageList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return defaultLanguageVillageList[oldItemPosition] == localLanguageVillageList[newItemPosition]
    }

}