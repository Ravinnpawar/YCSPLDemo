package com.ravindra.ycspldemo.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ravindra.ycspldemo.data.models.PropertyDetails

@Dao
interface PropertyDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun InsertData(property: PropertyDetails)

    @Query("SELECT * FROM Property")
    fun getPropertyDetails() : LiveData<List<PropertyDetails>>
}