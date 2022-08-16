package com.ravindra.ycspldemo.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import com.ravindra.ycspldemo.data.models.PropertyDetails
import com.ravindra.ycspldemo.data.room.PropertyDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class PropertyRepository {

    companion object{
        var propertyDatabase: PropertyDatabase? = null

        var propertyDetails: LiveData<PropertyDetails>? = null

        var propertyDetailsList: LiveData<List<PropertyDetails>>? = null

        fun initializeDB(context: Context) : PropertyDatabase {
            return PropertyDatabase.getDataseClient(context)
        }
        fun insertData(context: Context, propertName: String, lattitude: Double,longitude:Double) {

            propertyDatabase = initializeDB(context)

            CoroutineScope(IO).launch {
                val propertyDetails = PropertyDetails(propertName, lattitude,longitude)
                propertyDatabase!!.insertDAO().InsertData(propertyDetails)
            }

        }
        fun getPropertyDetails(context: Context) : LiveData<List<PropertyDetails>>? {

            propertyDatabase = initializeDB(context)

            propertyDetailsList = propertyDatabase!!.insertDAO().getPropertyDetails()

            return propertyDetailsList
        }



    }

}