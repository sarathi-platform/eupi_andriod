package com.nudge.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nudge.core.CASTE_TABLE
import com.nudge.core.model.CasteModel

@Entity(tableName = CASTE_TABLE)
data class CasteEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("casteId")
    @Expose
    @ColumnInfo(name = "casteId")
    val casteId: Int? = 1,

    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "id")
    var id: Int,

    @SerializedName("casteName")
    @Expose
    @ColumnInfo(name = "casteName")
    var casteName: String,

    @SerializedName("languageId")
    @Expose
    @ColumnInfo(name = "languageId")
    var languageId: Int
) {
    companion object {
        fun getCasteEntity(casteModel: CasteModel): CasteEntity {
            return CasteEntity(
                id = 0,
                casteId = casteModel.casteId,
                casteName = casteModel.casteName,
                languageId = casteModel.languageId
            )

        }

        fun getDefaultCasteListForLanguage(languageId: Int): List<CasteEntity> {
            when (languageId) {
                1 -> {
                    val castListForLanguage = mutableListOf<CasteEntity>()
                    castListForLanguage.add(
                        CasteEntity(
                            id = 1,
                            casteName = "सामान्य जाति",
                            languageId = languageId
                        )
                    )
                    castListForLanguage.add(
                        CasteEntity(
                            id = 2,
                            casteName = "अन्य पिछड़ी जाति",
                            languageId = languageId
                        )
                    )
                    castListForLanguage.add(
                        CasteEntity(
                            id = 3,
                            casteName = "अनुसूचित जाति",
                            languageId = languageId
                        )
                    )
                    castListForLanguage.add(
                        CasteEntity(
                            id = 4,
                            casteName = "अनुसूचित जनजाति",
                            languageId = languageId
                        )
                    )
                    return castListForLanguage
                }

                2 -> {
                    val castListForLanguage = mutableListOf<CasteEntity>()
                    castListForLanguage.add(
                        CasteEntity(
                            id = 1,
                            casteName = "GEN- General",
                            languageId = languageId
                        )
                    )
                    castListForLanguage.add(
                        CasteEntity(
                            id = 2,
                            casteName = "OBC- Other Backward Class",
                            languageId = languageId
                        )
                    )
                    castListForLanguage.add(
                        CasteEntity(
                            id = 3,
                            casteName = "SC- Scheduled Caste",
                            languageId = languageId
                        )
                    )
                    castListForLanguage.add(
                        CasteEntity(
                            id = 4,
                            casteName = "ST- Scheduled Tribes",
                            languageId = languageId
                        )
                    )

                    return castListForLanguage
                }

                3 -> {
                    val castListForLanguage = mutableListOf<CasteEntity>()
                    castListForLanguage.add(
                        CasteEntity(
                            id = 1,
                            casteName = "GEN- সাধারণ",
                            languageId = languageId
                        )
                    )
                    castListForLanguage.add(
                        CasteEntity(
                            id = 2,
                            casteName = "ওবিসি- অন্যান্য অনগ্রসর শ্রেণী",
                            languageId = languageId
                        )
                    )
                    castListForLanguage.add(
                        CasteEntity(
                            id = 3,
                            casteName = "SC- তফসিলি জাতি",
                            languageId = languageId
                        )
                    )
                    castListForLanguage.add(
                        CasteEntity(
                            id = 4,
                            casteName = "ST- তফসিলি উপজাতি",
                            languageId = languageId
                        )
                    )
                    return castListForLanguage
                }

                else -> {
                    return listOf()
                }
            }
        }
    }
}
