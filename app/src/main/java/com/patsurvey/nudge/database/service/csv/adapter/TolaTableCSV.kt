package com.patsurvey.nudge.database.service.csv.adapter

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.opencsv.bean.CsvBindByName
import com.patsurvey.nudge.database.DidiEntity
import com.patsurvey.nudge.database.TolaEntity
import com.patsurvey.nudge.database.service.csv.Exportable

data class TolaTableCSV(
    @CsvBindByName(column = "id")
    var id: Int,

    @CsvBindByName(column = "localUniqueId")
    var localUniqueId : String? = "",

    @CsvBindByName(column = "serverId")
    var serverId: Int = 0,

    @CsvBindByName(column = "name")
    var name : String,

    @CsvBindByName(column = "type")
    var type: String,

    @CsvBindByName(column = "latitude")
    var latitude: Double,

    @CsvBindByName(column = "longitude")
    var longitude: Double,

    @CsvBindByName(column = "villageId")
    var villageId: Int,

    @CsvBindByName(column = "status")
    val status: Int,

    @CsvBindByName(column = "createdDate")
    var createdDate: Long?=0,

    @CsvBindByName(column = "modifiedDate")
    var modifiedDate: Long?=0,

    @CsvBindByName(column = "localCreatedDate")
    var localCreatedDate: Long?=0,

    @CsvBindByName(column = "localModifiedDate")
    var localModifiedDate: Long?=0,

    @CsvBindByName(column = "needsToPost")
    var needsToPost: Boolean = true,

    @CsvBindByName(column = "transactionId")
    var transactionId: String? = ""
): Exportable
    fun List<TolaEntity>.toCsv() : List<TolaTableCSV> = map {
        TolaTableCSV(
            id = it.id,
            localUniqueId = it.localUniqueId,
            serverId = it.serverId,
            name = it.name,
            type = it.type,
            latitude = it.latitude,
            longitude = it.longitude,
            villageId = it.villageId,
            status = it.status,
            createdDate = it.createdDate,
            modifiedDate = it.modifiedDate,
            localCreatedDate = it.localCreatedDate,
            localModifiedDate = it.localModifiedDate,
            needsToPost = it.needsToPost,
            transactionId = it.transactionId
        )
    }
