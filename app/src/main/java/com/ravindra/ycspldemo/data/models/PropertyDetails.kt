package com.ravindra.ycspldemo.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Property")
data class PropertyDetails(
    @ColumnInfo(name = "propertName") val propertName: String,
    @ColumnInfo(name = "lattitude") val lattitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var Id: Int? = null
}