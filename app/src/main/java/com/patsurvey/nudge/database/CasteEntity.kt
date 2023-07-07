package com.patsurvey.nudge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patsurvey.nudge.utils.CASTE_TABLE

@Entity(tableName = CASTE_TABLE)
data class CasteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "casteId")
    val casteId: Int?=1,
    @ColumnInfo(name = "id")
    var id: Int,
    @ColumnInfo(name = "casteName")
    var casteName : String,
    @ColumnInfo(name = "languageId")
    var languageId : Int
) {
    companion object {
        fun getDefaultCasteListForLanguage(languageId: Int): List<CasteEntity> {
            return when (languageId) {
                1 -> {
                    val castListForLanguage = mutableListOf<CasteEntity>()
                    castListForLanguage.add(CasteEntity(id = 1, casteName = "सामान्य जाति", languageId = languageId))
                    castListForLanguage.add(CasteEntity(id = 2, casteName = "अन्य पिछड़ी जाति", languageId = languageId))
                    castListForLanguage.add(CasteEntity(id = 3, casteName = "अनुसूचित जाति", languageId = languageId ))
                    castListForLanguage.add(CasteEntity(id = 4, casteName = "अनुसूचित जनजाति", languageId = languageId))
                    castListForLanguage
                }
                2 -> {
                    val castListForLanguage = mutableListOf<CasteEntity>()
                    castListForLanguage.add(CasteEntity(id = 1, casteName = "GEN- General", languageId = languageId))
                    castListForLanguage.add(CasteEntity(id = 2, casteName = "OBC- Other Backward Class", languageId = languageId))
                    castListForLanguage.add(CasteEntity(id = 3, casteName = "SC- Scheduled Caste", languageId = languageId ))
                    castListForLanguage.add(CasteEntity(id = 4, casteName = "ST- Scheduled Tribes", languageId = languageId))

                    castListForLanguage
                }
                3 -> {
                    val castListForLanguage = mutableListOf<CasteEntity>()
                    castListForLanguage.add(CasteEntity(id = 1, casteName = "GEN- সাধারণ", languageId = languageId))
                    castListForLanguage.add(CasteEntity(id = 2, casteName = "ওবিসি- অন্যান্য অনগ্রসর শ্রেণী", languageId = languageId))
                    castListForLanguage.add(CasteEntity(id = 3, casteName = "SC- তফসিলি জাতি", languageId = languageId ))
                    castListForLanguage.add(CasteEntity(id = 4, casteName = "ST- তফসিলি উপজাতি", languageId = languageId))
                    castListForLanguage
                }
                else -> {
                    listOf()
                }
            }
        }
    }
}
