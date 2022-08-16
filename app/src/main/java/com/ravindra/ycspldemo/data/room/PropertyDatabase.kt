package com.ravindra.ycspldemo.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ravindra.ycspldemo.data.models.PropertyDetails

@Database(entities = arrayOf(PropertyDetails::class), version = 1, exportSchema = false)
abstract class PropertyDatabase:RoomDatabase() {

    abstract fun insertDAO():PropertyDAO
    companion object {

        @Volatile
        private var INSTANCE: PropertyDatabase? = null

        fun getDataseClient(context: Context) : PropertyDatabase {

            if (INSTANCE != null) return INSTANCE!!

            synchronized(this) {

                INSTANCE = Room
                    .databaseBuilder(context, PropertyDatabase::class.java, "PROPERTY_DATABASE")
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!

            }
        }

    }

}