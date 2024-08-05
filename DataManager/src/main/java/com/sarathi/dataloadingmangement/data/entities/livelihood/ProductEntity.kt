package com.sarathi.dataloadingmangement.data.entities.livelihood

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sarathi.dataloadingmangement.BLANK_STRING
import com.sarathi.dataloadingmangement.PRODUCT_TABLE_NAME
import com.sarathi.dataloadingmangement.model.response.Product


@Entity(tableName = PRODUCT_TABLE_NAME)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("primaryKey")
    @Expose
    @ColumnInfo(name = "primaryKey")
    var primaryKey: Int = 0,
    var id: Int,
    var userId: String,
    var name: String,
    var status: Int,
    var type: Int? = 0,

    ) {
    companion object {
        fun getProductEntity(
            userId: String,
            product: Product
        ): ProductEntity {

            return ProductEntity(
                primaryKey = 0,
                id = product.id ?: 0,
                userId = userId,
                name = product.name ?: BLANK_STRING,
                status = product.status ?: 0,
            )
        }

    }
}