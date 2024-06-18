package com.sarathi.dataloadingmangement.data.entities.smallGroup

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sarathi.dataloadingmangement.SMALL_GROUP_DIDI_MAPPING_TABLE
import com.sarathi.dataloadingmangement.model.response.SmallGroupMappingResponseModel

@Entity(tableName = SMALL_GROUP_DIDI_MAPPING_TABLE)
data class SmallGroupDidiMappingEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "userId")
    var userId: String?,

    @ColumnInfo(name = "smallGroupId")
    val smallGroupId: Int,

    @ColumnInfo(name = "smallGroupName")
    val smallGroupName: String,

    @ColumnInfo(name = "didiId")
    val didiId: Int,

    @ColumnInfo(name = "date")
    val date: Long,

    @ColumnInfo(name = "status")
    val status: Int
) {

    companion object {

        fun getSmallGroupDidiMappingEntityForDidi(
            userId: String,
            smallGroupId: Int,
            smallGroupName: String, status: Int, didiId: Int
        ): SmallGroupDidiMappingEntity {

            return SmallGroupDidiMappingEntity(
                id = 0,
                userId = userId,
                smallGroupId = smallGroupId,
                smallGroupName = smallGroupName,
                didiId = didiId,
                date = System.currentTimeMillis(),
                status = status
            )

        }

        fun getSmallGroupDidiMappingEntityListForSmallGroup(
            smallGroupMappingResponseModel: SmallGroupMappingResponseModel,
            userId: String,
            date: Long
        ): List<SmallGroupDidiMappingEntity> {

            val smallGroupDidiMappingEntityList = ArrayList<SmallGroupDidiMappingEntity>()

            smallGroupMappingResponseModel.beneficiaryIds.forEach { benId ->

                smallGroupDidiMappingEntityList.add(
                    SmallGroupDidiMappingEntity(
                        id = 0,
                        userId = userId,
                        smallGroupId = smallGroupMappingResponseModel.id,
                        smallGroupName = smallGroupMappingResponseModel.name,
                        didiId = benId,
                        date = date,
                        status = smallGroupMappingResponseModel.status
                    )
                )

            }

            return smallGroupDidiMappingEntityList

        }
    }

}
